package com.hao.loginlib;

import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hao.baselib.base.WaterPermissionActivity;
import com.hao.baselib.dialog.LoadingDialog;
import com.hao.baselib.utils.NullUtil;
import com.hao.baselib.utils.StatusBarUtil;
import com.hao.baselib.utils.ToastUtil;
import com.hao.initlib.ConfigRoute;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录页面
 * @author WaterWood
 */
@Route(path = ConfigRoute.LOGIN_ACTIVITY)
public class LoginActivity extends WaterPermissionActivity<LoginModel> implements LoginCallBack {

    @BindView(R2.id.ed_loginPhone)
    EditText ed_loginPhone;
    @BindView(R2.id.ed_loginPass)
    EditText ed_loginPass;
    @BindView(R2.id.cb_loginEye)
    CheckBox cb_loginEye;
    private LoadingDialog loadingDialog;

    @Override
    protected LoginModel getModelImp() {
        return new LoginModel(this, this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initWidget() {
        StatusBarUtil.setTransparent(this);
        loadingDialog = new LoadingDialog(this);
    }

    @OnClick({R2.id.tv_login, R2.id.cb_loginEye})
    public void onClick(View view) {
        if (view.getId() == R.id.tv_login){
            if (isEmpty()){
                loadingDialog.showDialog("加载中...");
                mModel.login(true);
            }
        }else if (view.getId() == R.id.cb_loginEye){
            if (cb_loginEye.isChecked()) {
                ed_loginPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                ed_loginPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        }
    }

    /**
     * 判空
     * @return
     */
    private boolean isEmpty(){
        if (NullUtil.isStringEmpty(ed_loginPhone.getText().toString().trim())) {
            ToastUtil.toastWord(this, "请输入手机号");
            return false;
        } else if (NullUtil.isStringEmpty(ed_loginPass.getText().toString().trim())) {
            ToastUtil.toastWord(this, "请输入密码");
            return false;
        }else{
            return true;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mModel.updateApk(true);
    }

    @Override
    public void loginSuccess() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
                ARouter.getInstance().build(ConfigRoute.HOME_ACTIVITY).navigation();
            }
        },3000);
    }

    @Override
    public void loginFailure(String message) {
        ToastUtil.toastWord(this,message);
    }

    @Override
    public void updateApkSuccess() {
        // TODO: 2021/1/12 启动版本更新
//        UpdateUtil.getInstance().startUpdate(this
//                , updateApkEntity.getData().getVersioncode()
//                , updateApkEntity.getData().getBbms().replaceAll("\\\\n","\n")
//                , updateApkEntity.getData().getIsgx() == 0 ? false : true
//                , updateApkEntity.getData().getGxdz()
//                , "pwgdqd.apk");
    }

    @Override
    public void updateApkFailure(String message) {
        ToastUtil.toastWord(this,message);
    }
}
