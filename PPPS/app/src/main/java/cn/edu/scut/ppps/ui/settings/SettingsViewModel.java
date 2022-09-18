package cn.edu.scut.ppps.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.List;

import cn.edu.scut.ppps.MainActivity;
import cn.edu.scut.ppps.Utils;
import cn.edu.scut.ppps.cloud.Tokens;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<String> cloudName1;
    private final MutableLiveData<String> cloudProvider1;
    private final MutableLiveData<String> cloudName2;
    private final MutableLiveData<String> cloudProvider2;
    private final MutableLiveData<String> imageNum;
    private final MutableLiveData<String> cameraResolution;
    private MainActivity mainActivity;


    public SettingsViewModel() {
        cloudName1 = new MutableLiveData<>();
        cloudProvider1 = new MutableLiveData<>();
        cloudName2 = new MutableLiveData<>();
        cloudProvider2 = new MutableLiveData<>();
        imageNum = new MutableLiveData<>();
        cameraResolution = new MutableLiveData<>();
    }

    public void setMainActivity(MainActivity activity) {
        mainActivity = activity;
    }

    public void setTexts() {
        Tokens tokens = mainActivity.getTokens();
        String tokenName1 = mainActivity.getTokenName1();
        String tokenName2 = mainActivity.getTokenName2();
        if (tokenName1 == null || tokenName2 == null) {
            cloudName1.setValue("请先配置云存储");
            cloudProvider1.setValue("请先配置云存储");
            cloudName2.setValue("请先配置云存储");
            cloudProvider2.setValue("请先配置云存储");
        } else {
            String provider1 = tokens.getToken(tokenName1).get("type");
            String provider2 = tokens.getToken(tokenName2).get("type");
            if (provider1.equals("aliyun")) {
                provider1 = "阿里云对象云存储";
            }
            if (provider2.equals("aliyun")) {
                provider2 = "阿里云对象云存储";
            }
            cloudName1.setValue(tokenName1);
            cloudProvider1.setValue(provider1);
            cloudName2.setValue(tokenName2);
            cloudProvider2.setValue(provider2);
        }
        try {
            String imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + File.separator + "Thumbnail" + File.separator;
            List<String> existFiles = Utils.getAllFile(imgPath);
            imageNum.setValue(String.valueOf(existFiles.size()));
        } catch (Exception e) {
            imageNum.setValue("0");
        }
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("cn.edu.scut.ppps_preferences", Context.MODE_PRIVATE);
        cameraResolution.setValue(sharedPreferences.getString("camera_resolution", "2K"));
    }

    public MutableLiveData<String> getCloudName1() {
        return cloudName1;
    }

    public MutableLiveData<String> getCloudProvider1() {
        return cloudProvider1;
    }

    public MutableLiveData<String> getCloudName2() {
        return cloudName2;
    }

    public MutableLiveData<String> getCloudProvider2() {
        return cloudProvider2;
    }

    public MutableLiveData<String> getImageNum() {
        return imageNum;
    }

    public MutableLiveData<String> getCameraResolution() {
        return cameraResolution;
    }
}