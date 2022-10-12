package cn.edu.scut.ppps;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.hao.baselib.base.WaterPermissionActivity;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import cn.edu.scut.ppps.cloud.AliOSS;
import cn.edu.scut.ppps.cloud.CloudService;
import cn.edu.scut.ppps.cloud.Tokens;
import cn.edu.scut.ppps.databinding.ActivityMainBinding;
import cn.edu.scut.ppps.gallary.AlbumCallback;
import cn.edu.scut.ppps.gallary.AlbumModel;
import cn.edu.scut.ppps.gallary.FolderBean;
import cn.edu.scut.ppps.gallary.ImageAdapter;
import cn.edu.scut.ppps.gallary.ListImageDirPopupWindow;

/**
 * Main Activity.
 * @author Cui Yuxin
 * @source https://blog.csdn.net/weixin_38322371/article/details/106312474
 */
public class MainActivity extends WaterPermissionActivity<AlbumModel> implements AlbumCallback, View.OnClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private Context context;
    private View view;
    // 控件四个
    public GridView mGridView;
    private RelativeLayout mBottomly;
    private TextView mDirName;
    private TextView mDirCount;
    // 图片数据集
    private List<String> mImages;
    // 对应当前文件夹的File对象
    private File mCurrentDir;
    // 当前文件夹中的文件个数
    private int mMaxCount;
    // 文件夹列表
    private List<FolderBean> mFolderBeans = new ArrayList<>();
    private Set<String> mDirPaths = new HashSet<String>();
    // 弹框
    private ListImageDirPopupWindow mDirPopupWindow;
    // 是否为批量处理
    private boolean multiSelect = false;
    // 已经选择图片的集合
    private ArrayList<String> listChoosePics = new ArrayList<>();
    // 执行中弹框
    private ProgressDialog mProgressDialog;
    // Pipeline
    private Pipeline pipeline;
    // Tokens
    private Tokens tokens;
    private String tokenName1 = null;
    private String tokenName2 = null;
    // 该参数负责子线程查询图片后通知主线程更新UI
    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Utils.UI) {
                showData();
                initDirPopupWindow();
            }
        }
    };
    // 该参数负责获取子线程执行结果
    @SuppressLint("HandlerLeak")
    private Handler algorithmHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Utils.START_ALGORITHM) {
                mProgressDialog = ProgressDialog.show(context, null, "正在执行...");
            } else if (msg.what == Utils.FINISH_ALGORITHM) {
                mProgressDialog.dismiss();
            } else if (msg.what == Utils.START_CLOUD) {
                mProgressDialog = ProgressDialog.show(context, null, "正在上传/下载...");
            } else if (msg.what == Utils.FINISH_CLOUD) {
                mProgressDialog.dismiss();
            } else if (msg.what == Utils.ERROR) {
                mProgressDialog.dismiss();
                Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
            } else if (msg.what == Utils.SUCCESS) {
                mProgressDialog.dismiss();
                Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.parse("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);
                deepRefresh();
            } else if (msg.what == Utils.THUMBNAIL_START) {
                mCurrentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                        + File.separator + "Thumbnail");
                refresh();
            }
        }
    };
    // 图片适配器
    private ImageAdapter mImgAdapter;

    /**
     * Create the main activity
     * @param savedInstanceState Bundle
     * @author Cui Yuxin
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (multiSelect) {
                    multiProcess();
                } else {
                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    startActivityForResult(intent, Utils.CAMERA_RESULT);
                }
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        initWidget();
        mBottomly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDirPopupWindow.setAnimationStyle(R.style.dir_popupwindow_anim);
                mDirPopupWindow.showAsDropDown(mBottomly, 0, 0);
                lightOff();
            }
        });
        context = this;
        view = binding.getRoot();
        try {
            tokens = new Tokens(context);
        } catch (Exception e) {
            e.printStackTrace();
            algorithmHandler.sendEmptyMessage(Utils.ERROR);
        }
        updateCloud();
    }

    /**
     * Update cloud and tokens.
     * @author Cui Yuxin
     */
    private void updateCloud() {
        SharedPreferences sharedPreferences = getSharedPreferences("cn.edu.scut.ppps_preferences", Context.MODE_PRIVATE);
        tokenName1 = sharedPreferences.getString("cloud_name1", null);
        tokenName2 = sharedPreferences.getString("cloud_name2", null);
        if (tokenName1 == null || tokenName2 == null ||
                tokenName1.equals("") || tokenName2.equals("") || tokenName1.equals(tokenName2)) {
            Toast.makeText(context, "请先设置云存储登录口令!", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> token1 = new HashMap<>();
        Map<String, String> token2 = new HashMap<>();
        token1.put("type", sharedPreferences.getString("cloud_provider1", null));
        token1.put("accessId", sharedPreferences.getString("cloud_id1", null));
        token1.put("accessSecret", sharedPreferences.getString("cloud_key1", null));
        token1.put("endpoint", sharedPreferences.getString("cloud_endpoint1", null));
        token1.put("bucketName", sharedPreferences.getString("cloud_bucket1", null));
        token1.put("filePath", sharedPreferences.getString("cloud_path1", null));
        token2.put("type", sharedPreferences.getString("cloud_provider2", null));
        token2.put("accessId", sharedPreferences.getString("cloud_id2", null));
        token2.put("accessSecret", sharedPreferences.getString("cloud_key2", null));
        token2.put("endpoint", sharedPreferences.getString("cloud_endpoint2", null));
        token2.put("bucketName", sharedPreferences.getString("cloud_bucket2", null));
        token2.put("filePath", sharedPreferences.getString("cloud_path2", null));
        try {
            tokens.updateToken(tokenName1, token1);
            tokens.updateToken(tokenName2, token2);
        } catch (IOException e) {
            e.printStackTrace();
            algorithmHandler.sendEmptyMessage(Utils.ERROR);
        }
        CloudService cloudService1 = new AliOSS(tokenName1, context, tokens);
        CloudService cloudService2 = new AliOSS(tokenName2, context, tokens);
        pipeline = new Pipeline(algorithmHandler, context, cloudService1, cloudService2);
    }

    /**
     * Create options menu.
     * @param menu menu
     * @author Cui Yuxin
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Callback from CameraActivity and PreviewActivity.
     * @param requestCode request code.
     * @param resultCode result code.
     * @param data data.
     * @author Cui Yuxin , Huang Zixi
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.CAMERA_RESULT && resultCode == RESULT_OK) {
            Uri imgUri = Uri.parse(data.getStringExtra("uri"));
            singleProcess(Utils.uri2Path(imgUri, context));
        } else if (requestCode == Utils.SETTINGS_UPDATE) {
            updateCloud();
        } else if(requestCode == Utils.PREVIEW_RESULT && resultCode == RESULT_OK){
            String path = data.getStringExtra("path");

            if (path.contains("Thumbnail")) {
                pipeline.decryptPipeline(new String[]{path});
            } else {
                pipeline.encryptPipeline(new String[]{path});
            }
        }
    }

    /**
     * Select menu item event.
     * @param item menu item
     * @author Cui Yuxin
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_muti) {
            multiSelect = !multiSelect;
            listChoosePics = new ArrayList<>();
            FloatingActionButton fab = binding.appBarMain.fab;
            if (multiSelect) {
                fab.setImageResource(R.drawable.ic_media_play);
            } else {
                fab.setImageResource(R.drawable.ic_menu_camera);
            }
            refresh();
        } else if (item.getItemId() == R.id.action_update) {
            if (pipeline == null) {
                updateCloud();
            } else {
                pipeline.thumbnailPipeline();
            }
            refresh();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Support navigate up.
     * @author Cui Yuxin
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Get album model.
     * @author Cui Yuxin
     */
    @Override
    protected AlbumModel getModelImp() {
        return new AlbumModel(this, this);
    }

    /**
     * Get content layout Id.
     * @author Cui Yuxin
     */
    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_home;
    }

    /**
     * Init widget.
     * @author Cui Yuxin
     */
    @Override
    protected void initWidget() {
        statusBarColor(R.color.white, false);
        mGridView = findViewById(R.id.mGridView);
        mBottomly = findViewById(R.id.mBottomly);
        mDirName = findViewById(R.id.mDirName);
        mDirCount = findViewById(R.id.mDirCount);
    }

    /**
     * Init data.
     * @author Cui Yuxin
     */
    @Override
    protected void initData() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "当前存储卡不可用！", Toast.LENGTH_SHORT).show();
            return;
        }
        requestPermission(READ_EXTERNAL_STORAGE);
        mBottomly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDirPopupWindow.setAnimationStyle(R.style.dir_popupwindow_anim);
                mDirPopupWindow.showAsDropDown(mBottomly, 0, 0);
                lightOff();
            }
        });
    }

    /**
     * Request permission.
     * @author Cui Yuxin
     */
    @Override
    protected void doSDRead() {
        requestPermission(WRITE_EXTERNAL_STORAGE);
    }

    /**
     * Read gallery images.
     * @author Cui Yuxin
     */
    @Override
    protected void doSDWrite() {
        new Thread() {
            @Override
            public void run() {
                Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = MainActivity.this.getContentResolver();
                Cursor cursor = cr.query(mImgUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png", "image/webp"},
                        MediaStore.Images.Media.DATE_MODIFIED);
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null) {
                        continue;
                    }
                    String dirPath = parentFile.getAbsolutePath();
                    FolderBean folderBean = null;
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        //当前文件夹之前没扫描到
                        mDirPaths.add(dirPath);
                        folderBean = new FolderBean();
                        folderBean.setDir(dirPath);
                        folderBean.setFirstImgPath(path);
                    }
                    if (parentFile.list() == null) {
                        continue;
                    }
                    int picSize = Objects.requireNonNull(parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".jpeg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".webp")) {
                                return true;
                            }
                            return false;
                        }
                    })).length;
                    folderBean.setCount(picSize);
                    mFolderBeans.add(folderBean);
                    if (picSize > mMaxCount) {
                        mMaxCount = picSize;
                        mCurrentDir = parentFile;
                    }
                }
                cursor.close();
                uiHandler.sendEmptyMessage(Utils.UI);
            }
        }.start();
    }

    /**
     * Display the data on the UI
     * @author CUi Yuxin
     */
    protected void showData() {
        if (mCurrentDir == null) {
            Toast.makeText(this, "未扫描到任何图片", Toast.LENGTH_SHORT).show();
            return;
        }
        mImages = Arrays.asList(Objects.requireNonNull(mCurrentDir.list()));
        mImgAdapter = new ImageAdapter(this, mImages, mCurrentDir.getAbsolutePath());
        mImgAdapter.setMutil(multiSelect);
        mGridView.setAdapter(mImgAdapter);
        mDirCount.setText(String.valueOf(mMaxCount));
        mDirName.setText(mCurrentDir.getName());
    }

    /**
     * Init dir popup window.
     * @author Cui Yuxin
     */
    protected void initDirPopupWindow() {
        mDirPopupWindow = new ListImageDirPopupWindow(this, mFolderBeans);
        mDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });
        mDirPopupWindow.setmListener(new ListImageDirPopupWindow.OnDirSelectedListener() {
            @Override
            public void onSelected(FolderBean folderBean) {
                if (multiSelect) {
                    listChoosePics = new ArrayList<>();
                }
                mCurrentDir = new File(folderBean.getDir());
                mImages = Arrays.asList(Objects.requireNonNull(mCurrentDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        if (filename.endsWith(".jpg")
                                || filename.endsWith(".jpeg")
                                || filename.endsWith(".png")
                                || filename.endsWith(".webp")) {
                            return true;
                        }
                        return false;
                    }
                })));
                mImgAdapter = new ImageAdapter(MainActivity.this, mImages, mCurrentDir.getAbsolutePath());
                mImgAdapter.setMutil(multiSelect);
                mGridView.setAdapter(mImgAdapter);
                mDirCount.setText(String.valueOf(mImages.size()));
                mDirName.setText(folderBean.getName());
                mDirPopupWindow.dismiss();
            }
        });
    }

    /**
     * Day mode.
     * @author Cui Yuxin
     */
    private void lightOn() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    /**
     * Night mode.
     * @author Cui Yuxin
     */
    private void lightOff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }

    /**
     * Bind event for view
     * @param v view
     * @author Cui Yuxin
     */
    @Override
    public void onClick(View v) {

    }

    /**
     * Add pictures to the selected collection.
     * @param imgPath The path of the picture to be added.
     * @author Cui Yuxin
     */
    public void addImgToList(String imgPath) {
        listChoosePics.add(imgPath);
    }

    /**
     * Delete the specified picture from the collection.
     * @param imgPath The path of the picture to be removed.
     * @author Cui Yuxin
     */
    public void removeImgFromList(String imgPath) {
        listChoosePics.remove(imgPath);
    }

    /**
     * Get picture collection.
     * @author Cui Yuxin
     */
    public ArrayList<String> getPicList() {
        return listChoosePics;
    }

    /**
     * Return if multiple choice.
     * @author Cui Yuxin
     */
    public boolean isMultiSelect() {
        return multiSelect;
    }

    /**
     * Single choice processing
     * @param path The path of the picture to be selected.
     * @author Cui Yuxin , Huang Zixi
     */
    public void singleProcess(String path) {
        if (pipeline == null) {
            updateCloud();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("imgPath", path);
        String cachePath = context.getCacheDir().getAbsolutePath();
        intent.putExtra("cachePath", cachePath);
        intent.setClass(getApplicationContext(), PreviewActivity.class);
        startActivityForResult(intent,Utils.PREVIEW_RESULT);
    }



    /**
     * Refresh the screen.
     * @author Cui Yuxin
     */
    public void refresh() {
        initWidget();
        initData();
    }

    /**
     * Deep refresh the screen.
     * @author Cui Yuxin
     */
    public void deepRefresh() {
        mFolderBeans = new ArrayList<>();
        mDirPaths = new HashSet<String>();
        doSDWrite();
    }

    /**
     * Multiple choice processing
     * @author Cui Yuxin
     */
    private void multiProcess() {
        if (listChoosePics.size() == 0) {
            Toast.makeText(context, "请选择图片！", Toast.LENGTH_SHORT).show();
        } else {
            String path = listChoosePics.get(0);
            if (pipeline == null) {
                updateCloud();
                return;
            }
            if (path.contains("Thumbnail")) {
                pipeline.decryptPipeline(listChoosePics.toArray(new String[0]));
            } else {
                pipeline.encryptPipeline(listChoosePics.toArray(new String[0]));
            }
        }
    }

    /**
     * Open Settings activity.
     * @author Cui Yuxin
     */
    public void callSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, Utils.SETTINGS_UPDATE);
    }

    /**
     * Get tokens.
     * @author Cui Yuxin
     */
    public Tokens getTokens() {
        return tokens;
    }

    /**
     * Get tokenName1.
     * @author Cui Yuxin
     */
    public String getTokenName1() {
        return tokenName1;
    }

    /**
     * Get tokenName2.
     * @author Cui Yuxin
     */
    public String getTokenName2() {
        return tokenName2;
    }

    /**
     * Turn on the floating button.
     * @author Cui Yuxin
     */
    public void turnOnFloatingButton() {
        FloatingActionButton fab = binding.appBarMain.fab;
        fab.show();
    }

    /**
     * Turn off the floating button.
     * @author Cui Yuxin
     */
    public void turnOffFloatingButton() {
        FloatingActionButton fab = binding.appBarMain.fab;
        fab.hide();
    }
}
