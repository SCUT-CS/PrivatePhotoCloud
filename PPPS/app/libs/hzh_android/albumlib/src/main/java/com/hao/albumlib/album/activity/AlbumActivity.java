package com.hao.albumlib.album.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hao.albumlib.R;
import com.hao.albumlib.album.bean.FolderBean;
import com.hao.albumlib.album.adapter.ImageAdapter;
import com.hao.albumlib.album.callback.AlbumCallback;
import com.hao.albumlib.album.dialog.ListImageDirPopupWindow;
import com.hao.albumlib.album.model.AlbumModel;
import com.hao.baselib.base.WaterPermissionActivity;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 相册
 * @author WaterWood
 */
public class AlbumActivity extends WaterPermissionActivity<AlbumModel> implements AlbumCallback,View.OnClickListener {

    /**
     * 控件四个
     */
    private GridView mGridView;
    private RelativeLayout mBottomly;
    private TextView mDirName;
    private TextView mDirCount;
    private TextView tv_complete;
    /**
     * 图片数据集
     */
    private List<String> mImgs;
    /**
     * 对应当前文件夹的File对象
     */
    private File mCurrentDir;
    /**
     * 当前文件夹中的文件个数
     */
    private int mMaxCount;
    /**
     * 文件夹列表
     */
    private List<FolderBean> mFolderBeans = new ArrayList<>();
    /**
     * 进度条弹框
     */
    private ProgressDialog mProgressDialog;
    /**
     * 弹框
     */
    private ListImageDirPopupWindow mDirPopupWindow;
    /**
     * 使用参数
     */
    public static final String MUTIL_PICS = "imgs";//多图回传标识
    public static final String SINGLE_PICS = "single_img";//单图回传标识
    public static final String IS_MUTIL = "is_mutil";//是否是多图选择
    private boolean isMutil;
    /**
     * 已经选择图片的集合
     */
    private ArrayList<String> listChoosePics = new ArrayList<>();

    /**
     * 该参数负责子线程查询图片后通知主线程更新UI
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x110){
                mProgressDialog.dismiss();//消失对话框
                data2View();
                initDirPopupWindow();
            }
        }
    };
    /**
     * 图片适配器
     */
    private ImageAdapter mImgAdapter;

    @Override
    protected AlbumModel getModelImp() {
        return new AlbumModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_album;
    }

    @Override
    protected void initWidget() {
        statusBarColor(R.color.white,false);
        mGridView = findViewById(R.id.mGridView);
        mBottomly = findViewById(R.id.mBottomly);
        mDirName = findViewById(R.id.mDirName);
        mDirCount = findViewById(R.id.mDirCount);
        tv_complete = findViewById(R.id.tv_complete);
        tv_complete.setOnClickListener(this);
        Intent intent = getIntent();
        isMutil = intent.getBooleanExtra(IS_MUTIL,false);
        if (!isMutil){
            //隐藏完成
            tv_complete.setVisibility(View.GONE);
            //隐藏多选对勾

        }
    }

    @Override
    protected void initData() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this,"当前存储卡不可用！",Toast.LENGTH_SHORT).show();
            return;
        }
        requestPermission(READ_EXTERNAL_STORAGE);
        mBottomly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDirPopupWindow.setAnimationStyle(R.style.dir_popupwindow_anim);
                mDirPopupWindow.showAsDropDown(mBottomly,0,0);
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
        mProgressDialog = ProgressDialog.show(this,null,"正在加载...");
        new Thread(){
            @Override
            public void run() {
                Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = AlbumActivity.this.getContentResolver();
                Cursor cursor = cr.query(mImgUri,null,MediaStore.Images.Media.MIME_TYPE+"=? or "+MediaStore
                        .Images.Media.MIME_TYPE+"=?",new String[]{"image/jpeg","image/png"},MediaStore
                        .Images.Media.DATE_MODIFIED);
                Set<String> mDirPaths = new HashSet<String>();
                while (cursor.moveToNext()){
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null){
                        continue;
                    }
                    String dirPath = parentFile.getAbsolutePath();
                    FolderBean folderBean = null;
                    if (mDirPaths.contains(dirPath)){
                        continue;
                    }else{
                        //当前文件夹之前没扫描到
                        mDirPaths.add(dirPath);
                        folderBean = new FolderBean();
                        folderBean.setDir(dirPath);
                        folderBean.setFirstImgPath(path);
                    }
                    if (parentFile.list() == null){
                        continue;
                    }
                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".jpeg")
                                    || filename.endsWith(".png")){
                                return true;
                            }
                            return false;
                        }
                    }).length;
                    folderBean.setCount(picSize);
                    mFolderBeans.add(folderBean);
                    if (picSize > mMaxCount){
                        mMaxCount = picSize;
                        mCurrentDir = parentFile;
                    }
                }
                cursor.close();
                mHandler.sendEmptyMessage(0x110);
            }
        }.start();
    }

    protected void data2View(){
        if (mCurrentDir == null){
            Toast.makeText(this,"未扫描到任何图片",Toast.LENGTH_SHORT).show();
            return;
        }
        mImgs = Arrays.asList(mCurrentDir.list());
        mImgAdapter = new ImageAdapter(this,mImgs,mCurrentDir.getAbsolutePath());
        mImgAdapter.setMutil(isMutil);
        mGridView.setAdapter(mImgAdapter);
        mDirCount.setText(mMaxCount+"");
        mDirName.setText(mCurrentDir.getName());
    }

    private void initDirPopupWindow(){
        mDirPopupWindow = new ListImageDirPopupWindow(this,mFolderBeans);
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
                                ||filename.endsWith(".jpeg")
                                ||filename.endsWith(".png")){
                            return true;
                        }
                        return false;
                    }
                }));
                mImgAdapter = new ImageAdapter(AlbumActivity.this,mImgs,mCurrentDir.getAbsolutePath());
                mImgAdapter.setMutil(isMutil);
                mGridView.setAdapter(mImgAdapter);
                mDirCount.setText(mImgs.size()+"");
                mDirName.setText(folderBean.getName());
                mDirPopupWindow.dismiss();
            }
        });
    }

    private void lightOn(){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    private void lightOff(){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_complete) {
            if (isMutil) {
                //多图
                Intent intent = getIntent();
                intent.putStringArrayListExtra(MUTIL_PICS, listChoosePics);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    /**
     * 添加图片到已选集合中
     * @param pathPic
     */
    public void setPicToList(String pathPic){
        listChoosePics.add(pathPic);
    }

    /**
     * 从集合中删除指定图片
     * @param pathPic
     */
    public void removePicFromList(String pathPic){
        listChoosePics.remove(pathPic);
    }

    /**
     * 获取图片集合
     * @return
     */
    public ArrayList<String> getPicList(){
        return listChoosePics;
    }

    /**
     * 是否多图
     * @return
     */
    public boolean isMutil() {
        return isMutil;
    }

    /**
     * 单选的处理
     */
    public void singleGet(String path){
        Intent intent = getIntent();
        intent.putExtra(SINGLE_PICS, path);
        setResult(RESULT_OK, intent);
        finish();
    }
}
