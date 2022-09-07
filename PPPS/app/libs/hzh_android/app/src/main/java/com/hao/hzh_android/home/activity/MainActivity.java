package com.hao.hzh_android.home.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hao.baselib.base.BaseActivity;
import com.hao.baselib.utils.ScreenUtil;
import com.hao.baselib.utils.ToastUtil;
import com.hao.big_pic.picdetail.BigPicActivity;
import com.hao.hzh_android.R;
import com.hao.hzh_android.application.HzhApplication;
import com.hao.hzh_android.home.callback.MainCallback;
import com.hao.hzh_android.home.callback.WebJdCallback;
import com.hao.hzh_android.home.model.MainModel;
import com.hao.initlib.ConfigRoute;
import com.hao.initlib.Student;
import com.hao.okhttplib.http.DisposeDataListener;
import com.hao.okhttplib.http.OkHttpException;
import com.hao.okhttplib.http.RequestCenter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 入口首页
 *
 * @author WaterWood
 */
public class MainActivity extends BaseActivity<MainModel> implements MainCallback, View.OnClickListener {

    @BindView(R.id.bt_cjs)
    Button bt_cjs;
    private Button bt_dtqx;
    private Button bt_qjsxw;
    private Button bt_sykj;
    private Button bt_sykj2;
    private Button bt_fsqq;
    private Button bt_tpjz;
    private Button bt_dtyl;
    private Button bt_xyqh;
    private Button bt_uil;
    private Button bt_downloadpic;
    private Button bt_mutil;
    private Button bt_lb;
    private Button bt_xcpz;
    private Button bt_ewm;
    private Button bt_video_easy;
    private Button bt_tb;
    private Button bt_tk;
    private Button bt_bbgx;
    private Button bt_hqz;
    private Button bt_wyjz;
    private Button bt_sjk;
    private Button bt_ddxz;
    private Button bt_login;

    @Override
    protected MainModel getModelImp() {
        return new MainModel(this, this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        bt_qjsxw = findViewById(R.id.bt_qjsxw);
        bt_sykj = findViewById(R.id.bt_sykj);
        bt_sykj2 = findViewById(R.id.bt_sykj2);
        bt_fsqq = findViewById(R.id.bt_fsqq);
        bt_tpjz = findViewById(R.id.bt_tpjz);
        bt_dtyl = findViewById(R.id.bt_dtyl);
        bt_xyqh = findViewById(R.id.bt_xyqh);
        bt_uil = findViewById(R.id.bt_uil);
        bt_downloadpic = findViewById(R.id.bt_downloadpic);
        bt_mutil = findViewById(R.id.bt_mutil);
        bt_lb = findViewById(R.id.bt_lb);
        bt_xcpz = findViewById(R.id.bt_xcpz);
        bt_ewm = findViewById(R.id.bt_ewm);
        bt_video_easy = findViewById(R.id.bt_video_easy);
        bt_tb = findViewById(R.id.bt_tb);
        bt_tk = findViewById(R.id.bt_tk);
        bt_bbgx = findViewById(R.id.bt_bbgx);
        bt_hqz = findViewById(R.id.bt_hqz);
        bt_wyjz = findViewById(R.id.bt_wyjz);
        bt_sjk = findViewById(R.id.bt_sjk);
        bt_ddxz = findViewById(R.id.bt_ddxz);
        bt_login = findViewById(R.id.bt_login);
        bt_qjsxw.setOnClickListener(this);
        bt_sykj.setOnClickListener(this);
        bt_sykj2.setOnClickListener(this);
        bt_fsqq.setOnClickListener(this);
        bt_tpjz.setOnClickListener(this);
        bt_dtyl.setOnClickListener(this);
        bt_xyqh.setOnClickListener(this);
        bt_uil.setOnClickListener(this);
        bt_downloadpic.setOnClickListener(this);
        bt_mutil.setOnClickListener(this);
        bt_lb.setOnClickListener(this);
        bt_xcpz.setOnClickListener(this);
        bt_ewm.setOnClickListener(this);
        bt_video_easy.setOnClickListener(this);
        bt_tb.setOnClickListener(this);
        bt_tk.setOnClickListener(this);
        bt_bbgx.setOnClickListener(this);
        bt_hqz.setOnClickListener(this);
        bt_wyjz.setOnClickListener(this);
        bt_sjk.setOnClickListener(this);
        bt_ddxz.setOnClickListener(this);
        bt_login.setOnClickListener(this);
        registerBus();
    }

    @OnClick({R.id.bt_cjs, R.id.bt_dtqx})
    void click(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.bt_cjs:
                //沉浸式状态栏
//                intent = new Intent(this, CjsActivity.class);
//                startActivity(intent);
                try {
                    Class c1 = Class.forName("com.hao.hzh_android.home.StudentBean");
                    Object object = c1.newInstance();
                    Method m = c1.getDeclaredMethod("getName");
                    m.setAccessible(true);
                    ToastUtil.toastWord(this, (String) m.invoke(object));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_dtqx:
                //动态权限
                intent = new Intent(this, DtqxActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.bt_qjsxw:
                //全局上下文
                ToastUtil.toastWord(HzhApplication.getInstance(), "成功获取全局上下文");
                break;
            case R.id.bt_sykj:
                //首页框架
                intent = new Intent(this, SyActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_sykj2:
                //第二套首页框架
                intent = new Intent(this, SyActivity2.class);
                startActivity(intent);
                break;
            case R.id.bt_fsqq:
                //发送网络请求
                RequestCenter.getInstance(this)
                        .get("http://192.168.0.155:8091/SysAppVersion/newAppVersion.json")
                        .setListener(new DisposeDataListener() {
                            @Override
                            public void onSuccess(Object responseObj) {
                                ToastUtil.toastWord(MainActivity.this, responseObj.toString());
                            }

                            @Override
                            public void onFailure(Object reasonobj) {
                                OkHttpException okHttpException = (OkHttpException) reasonobj;
                                ToastUtil.toastWord(MainActivity.this, okHttpException.getEmsg().toString());
                            }
                        })
                        .go();
                break;
            case R.id.bt_tpjz:
                //图片加载
                intent = new Intent(this, TpjzActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_dtyl:
                //大图预览
                ArrayList<String> list = new ArrayList<>();
                list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598242730783&di=6a429ba4044557d8cc3546245124e035&imgtype=0&src=http%3A%2F%2F00.minipic.eastday.com%2F20161229%2F20161229104552_78541dd68e6709a00941eedb4f67073d_6.jpeg");
                list.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1130945611,61423843&fm=26&gp=0.jpg");
                list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598242730782&di=477ecb3343a50b4b719471b37858f4dc&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201602%2F21%2F20160221124036_Zt5w4.thumb.700_0.jpeg");
                intent = new Intent(this, BigPicActivity.class);
                intent.putStringArrayListExtra("listPic", list);
                intent.putExtra("site", 1);
                startActivity(intent);
                break;
            case R.id.bt_xyqh:
                //兴业银行
                intent = new Intent(this, XyyhActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_uil:
                //UIL图片加载
                intent = new Intent(this, TpjzActivity2.class);
                startActivity(intent);
                break;
            case R.id.bt_downloadpic:
                //带下载的图片加载器
                intent = new Intent(this, TpjzActivity3.class);
                startActivity(intent);
                break;
            case R.id.bt_mutil:
                //多布局列表
                intent = new Intent(this, MutilActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_lb:
                //轮播图
                intent = new Intent(this, LbActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_xcpz:
                //相册和拍照
                intent = new Intent(this, XcpzActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_ewm:
                //二维码扫描
                intent = new Intent(this, EwmActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_video_easy:
                //简单视频制作
                intent = new Intent(this, VideoEasyActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_tb:
                //图表
                intent = new Intent(this, ChartActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_tk:
                //弹框
                intent = new Intent(this, DialogActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_bbgx:
                //版本更新
                intent = new Intent(this, UpdateActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_hqz:
                //获取值和使用黄油刀的Adapter
                intent = new Intent(this, GetValueActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_wyjz:
                //网页加载
                intent = new Intent(this, WebJdActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_sjk:
                //数据库操作
                intent = new Intent(this, DbHandleActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_ddxz:
                //断点续传
                intent = new Intent(this, DdxcActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_login:
                //跳转登录页
                ARouter.getInstance().build(ConfigRoute.LOGIN_ACTIVITY)
                        .navigation();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBus(this);
    }

    @Override
    protected void receiveBusInfo(Object messageEvent) {
        Student student = (Student) messageEvent;
    }
}