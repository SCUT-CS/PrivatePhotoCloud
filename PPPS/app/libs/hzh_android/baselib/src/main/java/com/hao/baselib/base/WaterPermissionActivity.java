package com.hao.baselib.base;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 动态权限处理专用类
 * Created by WaterWood on 2018/5/9.
 */
public abstract class WaterPermissionActivity<T extends MvcBaseModel> extends BaseActivity<T> {
    //一些常用的权限名称
    protected static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;//写SD卡权限
    protected static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;//读SD卡权限
    protected static final String CAMERA = Manifest.permission.CAMERA;//相机权限
    protected static final String REQUEST_INSTALL_PACKAGES = Manifest.permission.REQUEST_INSTALL_PACKAGES;//安装APK权限
    protected static final String CALL_PHONE = Manifest.permission.CALL_PHONE;//拨打电话权限
    protected static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    protected static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    protected static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;

    //各权限对应的RequestCode
    protected static final int WRITE_EXTERNAL_STORAGE_RC = 0x01;//写SD卡权限
    protected static final int READ_EXTERNAL_STORAGE_RC = 0x02;//读SD卡权限
    protected static final int CAMERA_RC = 0x03;//相机权限
    protected static final int REQUEST_INSTALL_PACKAGES_RC = 0x04;//安装APK权限
    protected static final int CALL_PHONE_RC = 0x05;//拨打电话权限
    protected static final int ACCESS_COARSE_LOCATION_RC = 0x06;
    protected static final int ACCESS_FINE_LOCATION_RC = 0x07;
    protected static final int READ_PHONE_STATE_RC = 0x08;

    /**
     * 请求没有请求的权限
     *
     * @param permissions
     * @return true：完成请求权限操作，false：有不认识的权限
     */
    public boolean requestPermission(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0以上才需要进行动态权限的请求
            int requestCode;
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    //应用没有该权限，请求权限前先获取该权限的requestCode
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[i])){
                        //之前被拒绝了，进行别的相关操作
                        final Dialog commonDialog = new Dialog(this);
                    }else{
                        if (permissions[i].equals(WRITE_EXTERNAL_STORAGE)) {
                            requestCode = WRITE_EXTERNAL_STORAGE_RC;
                        } else if (permissions[i].equals(READ_EXTERNAL_STORAGE)) {
                            requestCode = READ_EXTERNAL_STORAGE_RC;
                        } else if (permissions[i].equals(CAMERA)) {
                            requestCode = CAMERA_RC;
                        } else if (permissions[i].equals(REQUEST_INSTALL_PACKAGES)) {
                            requestCode = REQUEST_INSTALL_PACKAGES_RC;
                        } else if (permissions[i].equals(CALL_PHONE)) {
                            requestCode = CALL_PHONE_RC;
                        } else if (permissions[i].equals(ACCESS_COARSE_LOCATION)) {
                            requestCode = ACCESS_COARSE_LOCATION_RC;
                        } else if (permissions[i].equals(ACCESS_FINE_LOCATION)) {
                            requestCode = ACCESS_FINE_LOCATION_RC;
                        } else if (permissions[i].equals(READ_PHONE_STATE)) {
                            requestCode = READ_PHONE_STATE_RC;
                        } else {
                            //有不认识的权限
                            return false;
                        }
                        //请求该权限
                        ActivityCompat.requestPermissions(this, new String[]{permissions[i]}, requestCode);
                    }
                } else {
                    //应用有该权限
                    if (permissions[i].equals(WRITE_EXTERNAL_STORAGE)) {
                        doSDWrite();
                    } else if (permissions[i].equals(READ_EXTERNAL_STORAGE)) {
                        doSDRead();
                    } else if (permissions[i].equals(CAMERA)) {
                        doCamera();
                    } else if (permissions[i].equals(REQUEST_INSTALL_PACKAGES)) {
                        doInstall();
                    } else if (permissions[i].equals(CALL_PHONE)) {
                        doPhone();
                    } else if (permissions[i].equals(ACCESS_COARSE_LOCATION)) {
                        doAccessCoarseLocation();
                    }else if (permissions[i].equals(ACCESS_FINE_LOCATION)) {
                        doAccessFineLocation();
                    }else if (permissions[i].equals(READ_PHONE_STATE)) {
                        doReadPhoneState();
                    }else {
                        //有不认识的权限
                        return false;
                    }
                }
            }
        } else {
            if (permissions[0].equals(WRITE_EXTERNAL_STORAGE)) {
                doSDWrite();
            } else if (permissions[0].equals(READ_EXTERNAL_STORAGE)) {
                doSDRead();
            } else if (permissions[0].equals(CAMERA)) {
                doCamera();
            } else if (permissions[0].equals(REQUEST_INSTALL_PACKAGES)) {
                doInstall();
            } else if (permissions[0].equals(CALL_PHONE)) {
                doPhone();
            } else if (permissions[0].equals(ACCESS_COARSE_LOCATION)) {
                doAccessCoarseLocation();
            }else if (permissions[0].equals(ACCESS_FINE_LOCATION)) {
                doAccessFineLocation();
            }else if (permissions[0].equals(READ_PHONE_STATE)) {
                doReadPhoneState();
            }else {
                //有不认识的权限
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_RC:
                //写SD卡权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意了权限申请
                    doSDWrite();
                } else {
                    //用户拒绝了权限申请
                    dontGot();
                }
                break;
            case READ_EXTERNAL_STORAGE_RC:
                //读SD卡权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意了权限申请
                    doSDRead();
                } else {
                    //用户拒绝了权限申请
                    dontGot();
                }
                break;
            case CAMERA_RC:
                //相机权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意了权限申请
                    doCamera();
                } else {
                    //用户拒绝了权限申请
                    dontGot();
                }
                break;
            case REQUEST_INSTALL_PACKAGES_RC:
                //安装APK权限
                doInstall();
                break;
            case CALL_PHONE_RC:
                //拨打电话权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意了权限申请
                    doPhone();
                } else {
                    //用户拒绝了权限申请
                    dontGot();
                }
                break;
            case ACCESS_COARSE_LOCATION_RC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意了权限申请
                    doAccessCoarseLocation();
                } else {
                    //用户拒绝了权限申请
                    dontGot();
                }
                break;
            case ACCESS_FINE_LOCATION_RC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意了权限申请
                    doAccessFineLocation();
                } else {
                    //用户拒绝了权限申请
                    dontGot();
                }
                break;
            case READ_PHONE_STATE_RC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意了权限申请
                    doReadPhoneState();
                } else {
                    //用户拒绝了权限申请
                    dontGot();
                }
                break;
        }
    }

    /**
     * 写SD卡回调
     */
    protected void doSDWrite() {

    }

    /**
     * 读SD卡回调
     */
    protected void doSDRead() {

    }

    /**
     * 相机回调
     */
    protected void doCamera() {

    }

    /**
     * 安装APK回调
     */
    protected void doInstall() {

    }

    /**
     * 拨打电话回掉
     */
    protected void doPhone() {

    }

    protected void doAccessCoarseLocation(){

    }

    protected void doAccessFineLocation(){

    }

    protected void doReadPhoneState(){

    }

    /**
     * 未获取权限回调
     */
    protected void dontGot() {

    }
}
