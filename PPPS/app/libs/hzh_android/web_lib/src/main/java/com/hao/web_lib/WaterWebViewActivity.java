package com.hao.web_lib;

import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.hao.baselib.base.MvcBaseModel;
import com.hao.baselib.base.WaterPermissionActivity;
import com.hao.baselib.utils.NullUtil;
import com.hao.baselib.utils.ToastUtil;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

/**
 * 加载网页页面
 * 说明：该抽象类提供了如下功能
 * 1.基本网页加载
 * 2.js调用原生方法，自定义注入名称和注入类
 * 3.原生调用js方法，带参数和不带参数两种
 * 4.网页加载失败调用的本地页面，地址举例："file:///android_asset/zanwu.html"
 * 5.网页加载结束时调用的方法
 * 6.网页名称的获取
 * 7.获取原生webview控件对象
 * @author WaterWood
 */
public abstract class WaterWebViewActivity<T extends MvcBaseModel> extends WaterPermissionActivity<T>{

    private AgentWeb mAgentWeb;
    private boolean isLoadSuccess = true;
    private String webName;

    @Override
    protected void initWidget() {
        statusBarColor(R.color.white, false);
        //初始化网络请求进度框
        if (NullUtil.isStringEmpty(webUrl())) {
            ToastUtil.toastWord(this, "未传入url");
            finish();
        } else {
            mAgentWeb = AgentWeb.with(this)
                    .setAgentWebParent(parentView(), new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .setWebChromeClient(mWebChromeClient)
                    .setWebViewClient(mWebViewClient)
                    .createAgentWeb()
                    .ready()
                    .go(webUrl());
            if (jsCallJava() != null && !NullUtil.isStringEmpty(jsCallJavaName())) {
                mAgentWeb.getJsInterfaceHolder().addJavaObject(jsCallJavaName(), jsCallJava());
            }
        }
    }

    /**
     * 客户端设置
     */
    private WebViewClient mWebViewClient = new WebViewClient(){
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (isLoadSuccess) {
                String errorUrlStr = errorUrl();
                if (!NullUtil.isStringEmpty(errorUrlStr)){
                    view.loadUrl(errorUrlStr);
                    isLoadSuccess = false;
                }
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            finishLoad();
        }
    };

    private WebChromeClient mWebChromeClient=new WebChromeClient(){
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            webName = title;
        }
    };

    /**
     * 重写返回按钮控制页面返回
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (mAgentWeb.back()){
                mAgentWeb.getWebCreator().getWebView().goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //=======================================以下是子类重写的方法====================================================

    /**
     * 网页加载失败返回的地址，地址举例"file:///android_asset/zanwu.html"
     */
    protected String errorUrl(){
        return null;
    }

    /**
     * 网页加载结束时调用
     */
    protected void finishLoad(){

    }

    /**
     * js调用java类，该类每个方法都要加一个注解 @JavascriptInterface
     * @return
     */
    protected Object jsCallJava(){
        return null;
    }

    /**
     * js调用java类标志名称
     * @return
     */
    protected String jsCallJavaName(){
        return null;
    }

    /**
     * 调用js方法，方法带参数
     * @param method
     * @param params
     */
    protected void javaCalljs(String method, String... params){
        mAgentWeb.getJsAccessEntrace().quickCallJs(method,params);
    }

    /**
     * 调用js方法，方法不带参数
     * @param method
     */
    protected void javaCalljs(String method){
        mAgentWeb.getJsAccessEntrace().quickCallJs(method);
    }

    /**
     * 必须复写的父控件
     * @return
     */
    protected abstract ViewGroup parentView();

    /**
     * 必须复写的url
     * @return
     */
    protected abstract String webUrl();

    //===========================================以下是子类可用的方法================================================

    /**
     * 对外开放：获取网页名称
     * @return
     */
    protected String getWebName() {
        return webName;
    }

    /**
     * 获取webview的方法
     * @return
     */
    protected WebView getWebView(){
        return mAgentWeb.getWebCreator().getWebView();
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }
}
