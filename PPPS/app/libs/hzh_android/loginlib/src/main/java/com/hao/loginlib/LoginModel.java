package com.hao.loginlib;

import android.content.Context;
import com.hao.baselib.base.MvcBaseModel;

/**
 * 登录Model
 * @author WaterWood
 */
public class LoginModel extends MvcBaseModel<LoginCallBack> {

    public LoginModel(Context context, LoginCallBack callback) {
        super(context, callback);
    }

    /**
     * 登录
     */
    public void login(boolean flag) {
        if (flag){
            callback.loginSuccess();
        }else{
            callback.loginFailure("登录失败");
        }
    }

    /**
     * 版本更新
     */
    public void updateApk(boolean flag) {
        if (flag){
            callback.updateApkSuccess();
        }else{
            callback.updateApkFailure("更新失败");
        }
    }
}
