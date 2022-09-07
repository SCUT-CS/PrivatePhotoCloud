package com.hao.hzh_android.home.activity;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.hao.baselib.base.BaseActivity;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.ScewmCallback;
import com.hao.hzh_android.home.model.ScewmModel;
import com.hao.zxinglib.zxing.util.QrCodeUtils;

/**
 * 生成二维码页面
 * @author WaterWood
 */
public class ScewmActivity extends BaseActivity<ScewmModel> implements ScewmCallback, View.OnClickListener {

    private EditText et;
    private Button bt_sc;
    private ImageView iv_show;

    @Override
    protected ScewmModel getModelImp() {
        return new ScewmModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_scewm;
    }

    @Override
    protected void initWidget() {
        et = findViewById(R.id.et);
        bt_sc = findViewById(R.id.bt_sc);
        iv_show = findViewById(R.id.iv_show);
        bt_sc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_sc:
                String str = et.getText().toString().trim();
                Bitmap bitmap = QrCodeUtils.createQRCode(str);
                iv_show.setImageBitmap(bitmap);
                break;
        }
    }
}
