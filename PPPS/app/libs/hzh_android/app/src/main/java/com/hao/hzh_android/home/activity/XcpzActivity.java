package com.hao.hzh_android.home.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.hao.albumlib.album.activity.AlbumActivity;
import com.hao.albumlib.takephoto.TakePhotoUtils;
import com.hao.baselib.base.WaterPermissionActivity;
import com.hao.baselib.utils.PathGetUtil;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.XcpzCallback;
import com.hao.hzh_android.home.model.XcpzModel;
import com.hao.imageloadbydown.imageload.DownloadImageLoader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 相册和拍照
 * @author WaterWood
 */
public class XcpzActivity extends WaterPermissionActivity<XcpzModel> implements XcpzCallback, View.OnClickListener {

    private Button bt_xc;
    private Button bt_pz;
    private Button bt_dtxc;
    private TextView tv_path;
    private ImageView iv_show;
    private final int MUTIL_CHOOSE = 0x01;
    private final int SINGLE_CHOOSE = 0x02;
    private final int TAKE_PHOTO = 0x03;
    private int currentType;

    @Override
    protected XcpzModel getModelImp() {
        return new XcpzModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_xcpz;
    }

    @Override
    protected void initWidget() {
        //初始化控件
        bt_xc = findViewById(R.id.bt_xc);
        bt_pz = findViewById(R.id.bt_pz);
        tv_path = findViewById(R.id.tv_path);
        bt_dtxc = findViewById(R.id.bt_dtxc);
        iv_show = findViewById(R.id.iv_show);
        bt_xc.setOnClickListener(this);
        bt_pz.setOnClickListener(this);
        bt_dtxc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_xc:
                //多图相册
                currentType = MUTIL_CHOOSE;
                requestPermission(WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.bt_dtxc:
                //单图相册
                currentType = SINGLE_CHOOSE;
                requestPermission(WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.bt_pz:
                //拍照,这里需要请求动态权限
                currentType = TAKE_PHOTO;
                requestPermission(WRITE_EXTERNAL_STORAGE);
                break;
        }
    }

    @Override
    protected void doSDWrite() {
        requestPermission(READ_EXTERNAL_STORAGE);
    }

    @Override
    protected void doSDRead() {
        //这里根据分类进行下一步操作
        if (currentType == MUTIL_CHOOSE){
            //多图选择
            Intent intent0 = new Intent(this, AlbumActivity.class);
            intent0.putExtra(AlbumActivity.IS_MUTIL,true);
            startActivityForResult(intent0,0x01);
        }else if (currentType == SINGLE_CHOOSE){
            //单图选择
            Intent intent1 = new Intent(this, AlbumActivity.class);
            intent1.putExtra(AlbumActivity.IS_MUTIL,false);
            startActivityForResult(intent1,0x02);
        }else{
            //拍照
            requestPermission(CAMERA);
        }
    }

    @Override
    protected void doCamera() {
        //这里调用拍照的方法
        List<String> listPath = new ArrayList<>();
        listPath.add("camera");
        //============拍照调用三步走============
        //1.调用相机并传入一个文件夹路径，获取一个文件路径
        File filePic = TakePhotoUtils.startCamera(this, PathGetUtil.getLongwayPath(this,listPath));
        if (filePic!=null){
            //2.赋值给父类的这个路径
            filePicCamera = filePic;
        }
    }

    /**
     * 3.获取拍照的结果
     * @param path
     */
    @Override
    protected void getCameraPath(String path) {
        tv_path.setText(path);
        DownloadImageLoader.getInstance()
                .loadRoundImage(this
                        ,path
                        ,iv_show);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case 0x01:
                    //这句是重点，获取多个路径
                    ArrayList<String> listPics = data.getStringArrayListExtra(AlbumActivity.MUTIL_PICS);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < listPics.size(); i++) {
                        stringBuilder.append(listPics.get(i));
                        stringBuilder.append("\n");
                    }
                    tv_path.setText(stringBuilder.toString());
                    DownloadImageLoader.getInstance()
                            .loadImage(this
                                    ,listPics.get(0)
                                    ,R.mipmap.ic_seize
                                    ,R.mipmap.ic_error
                                    ,iv_show);
                    break;
                case 0x02:
                    //这句是重点，获取单个路径
                    String filePath = data.getStringExtra(AlbumActivity.SINGLE_PICS);
                    tv_path.setText(filePath);
                    DownloadImageLoader.getInstance()
                            .loadCornerImage(this
                                    ,filePath
                                    ,20
                                    ,iv_show);
                    break;
            }
        }
    }
}
