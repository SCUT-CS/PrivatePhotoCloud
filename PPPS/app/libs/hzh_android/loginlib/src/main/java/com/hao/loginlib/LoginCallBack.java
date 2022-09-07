package com.hao.loginlib;

import com.hao.baselib.base.MvcBaseCallBack;

/**
 * 登录接口回调
 * @author WaterWood
 */
public interface LoginCallBack extends MvcBaseCallBack {

    /**
     * 登录回调接口
     */
    void loginSuccess();
    void loginFailure(String message);

    /**
     * 版本更新回调接口
     */
    void updateApkSuccess();
    void updateApkFailure(String message);
}
