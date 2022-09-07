package com.hao.hzh_android.home.activity;

import android.content.Intent;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hao.albumlib.album.activity.AlbumActivity;
import com.hao.baselib.base.WaterPermissionActivity;
import com.hao.baselib.utils.ToastUtil;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.EwmCallback;
import com.hao.hzh_android.home.model.EwmModel;
import com.hao.zxinglib.zxing.app.CaptureActivity;
import com.hao.zxinglib.zxing.inner.QrcodeScanInterface;
import com.hao.zxinglib.zxing.util.QrCodeUtils;

/**
 * 二维码扫码页面
 * @author WaterWood
 */
public class EwmActivity extends WaterPermissionActivity<EwmModel> implements EwmCallback, View.OnClickListener {

    private Button bt_qrcode;
    private Button bt_scewm;
    private Button bt_xctp;
    private static final int REQUEST_QRCODE = 0x01;
    private static final int CHOOSE_PIC = 0x02;

    @Override
    protected EwmModel getModelImp() {
        return new EwmModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_ewm;
    }

    @Override
    protected void initWidget() {
        bt_qrcode = findViewById(R.id.bt_qrcode);
        bt_scewm = findViewById(R.id.bt_scewm);
        bt_xctp = findViewById(R.id.bt_xctp);
        bt_qrcode.setOnClickListener(this);
        bt_scewm.setOnClickListener(this);
        bt_xctp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.bt_qrcode:
                //跳转到二维码
                intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_QRCODE);
                break;
            case R.id.bt_scewm:
                //生成二维码
                intent = new Intent(this, ScewmActivity.class);
                startActivityForResult(intent, REQUEST_QRCODE);
                break;
            case R.id.bt_xctp:
                //从相册选择图片识别
                Intent intent1 = new Intent(this, AlbumActivity.class);
                intent1.putExtra(AlbumActivity.IS_MUTIL,false);
                startActivityForResult(intent1,CHOOSE_PIC);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_QRCODE:
                    //正常扫码
                    String code = data.getStringExtra("SCAN_RESULT");
                    ToastUtil.toastWord(this,code);
                    break;
                case CHOOSE_PIC:
                    String filePath = data.getStringExtra(AlbumActivity.SINGLE_PICS);
                    //扫描图片
                    QrCodeUtils.scanQrCodeImage(this, filePath, new QrcodeScanInterface() {
                        @Override
                        public void scanSuccess(String info) {
                            ToastUtil.toastWord(EwmActivity.this,info);
                        }

                        @Override
                        public void scanFailure(String info) {
                            ToastUtil.toastWord(EwmActivity.this,info);
                        }
                    });
                    break;
            }
        }
    }
}
