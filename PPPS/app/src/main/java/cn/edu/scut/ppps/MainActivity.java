package cn.edu.scut.ppps;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
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

import com.google.android.material.navigation.NavigationView;
import com.hao.baselib.base.WaterPermissionActivity;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.edu.scut.ppps.databinding.ActivityMainBinding;

/**
 * Main Activity
 *
 * @author Cui Yuxin
 * @source https://blog.csdn.net/weixin_38322371/article/details/106312474
 */
public class MainActivity extends WaterPermissionActivity<AlbumModel> implements AlbumCallback, View.OnClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    // 控件四个
    public GridView mGridView;
    private RelativeLayout mBottomly;
    private TextView mDirName;
    private TextView mDirCount;
    // 图片数据集
    private List<String> mImgs;
    // 对应当前文件夹的File对象
    private File mCurrentDir;
    // 当前文件夹中的文件个数
    private int mMaxCount;
    // 文件夹列表
    private List<FolderBean> mFolderBeans = new ArrayList<>();
    private Set<String> mDirPaths = new HashSet<String>();
    // 弹框
    private ListImageDirPopupWindow mDirPopupWindow;
    // 使用参数
    private boolean isMutil = false;
    // 已经选择图片的集合
    private ArrayList<String> listChoosePics = new ArrayList<>();
    // 该参数负责子线程查询图片后通知主线程更新UI
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x110) {
                // mProgressDialog.dismiss();//消失对话框
                data2View();
                initDirPopupWindow();
            }
        }
    };
    // 图片适配器
    private ImageAdapter mImgAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "拍照", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                // TODO 获取拍照的url并进行单图片操作
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CameraActivity.class);
                //intent.putExtra("")
                startActivity(intent);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // TODO 打开其他位置的图片
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 2);
        } else if (item.getItemId() == R.id.action_muti) {
            isMutil = !isMutil;
            listChoosePics = new ArrayList<>();
            refresh();
        } else if (item.getItemId() == R.id.action_next) {
            if (isMutil) {
                // TODO 完成多选逻辑
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected AlbumModel getModelImp() {
        return new AlbumModel(this, this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initWidget() {
        statusBarColor(R.color.white, false);
        mGridView = findViewById(R.id.mGridView);
        mBottomly = findViewById(R.id.mBottomly);
        mDirName = findViewById(R.id.mDirName);
        mDirCount = findViewById(R.id.mDirCount);
    }

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

    @Override
    protected void doSDRead() {
        requestPermission(WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void doSDWrite() {
        //mProgressDialog = ProgressDialog.show(this,null,"正在加载...");
        new Thread() {
            @Override
            public void run() {
                Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = MainActivity.this.getContentResolver();
                Cursor cursor = cr.query(mImgUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore
                        .Images.Media.MIME_TYPE + "=?", new String[]{"image/jpeg", "image/png"}, MediaStore
                        .Images.Media.DATE_MODIFIED);
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
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
                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".jpeg")
                                    || filename.endsWith(".png")) {
                                return true;
                            }
                            return false;
                        }
                    }).length;
                    folderBean.setCount(picSize);
                    mFolderBeans.add(folderBean);
                    if (picSize > mMaxCount) {
                        mMaxCount = picSize;
                        mCurrentDir = parentFile;
                    }
                }
                cursor.close();
                mHandler.sendEmptyMessage(0x110);
            }
        }.start();
    }

    protected void data2View() {
        if (mCurrentDir == null) {
            Toast.makeText(this, "未扫描到任何图片", Toast.LENGTH_SHORT).show();
            return;
        }
        mImgs = Arrays.asList(mCurrentDir.list());
        mImgAdapter = new ImageAdapter(this, mImgs, mCurrentDir.getAbsolutePath());
        mImgAdapter.setMutil(isMutil);
        mGridView.setAdapter(mImgAdapter);
        mDirCount.setText(mMaxCount + "");
        mDirName.setText(mCurrentDir.getName());
    }

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
                mCurrentDir = new File(folderBean.getDir());
                mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        if (filename.endsWith(".jpg")
                                || filename.endsWith(".jpeg")
                                || filename.endsWith(".png")) {
                            return true;
                        }
                        return false;
                    }
                }));
                mImgAdapter = new ImageAdapter(MainActivity.this, mImgs, mCurrentDir.getAbsolutePath());
                mImgAdapter.setMutil(isMutil);
                mGridView.setAdapter(mImgAdapter);
                mDirCount.setText(mImgs.size() + "");
                mDirName.setText(folderBean.getName());
                mDirPopupWindow.dismiss();
            }
        });
    }

    private void lightOn() {
        // TODO 修改颜色
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    private void lightOff() {
        // TODO 修改颜色
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 添加图片到已选集合中
     */
    public void setPicToList(String pathPic) {
        listChoosePics.add(pathPic);
    }

    /**
     * 从集合中删除指定图片
     */
    public void removePicFromList(String pathPic) {
        listChoosePics.remove(pathPic);
    }

    /**
     * 获取图片集合
     */
    public ArrayList<String> getPicList() {
        return listChoosePics;
    }

    /**
     * 是否多图
     */
    public boolean isMutil() {
        return isMutil;
    }

    /**
     * 单选的处理
     */
    public void singleGet(String path) {
        // TODO 单图的处理
    }

    /**
     * 刷新界面
     */
    public void refresh() {
        initWidget();
        initData();
    }
}
