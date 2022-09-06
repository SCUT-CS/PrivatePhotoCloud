package com.hao.hzh_android.home.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.hao.hzh_android.R;
import com.hao.imageloadbydown.imageload.DownloadImageLoader;
import java.util.ArrayList;

/**
 * 轮播图适配器
 * @author WaterWood
 */
public class HotSalePagerAdapter extends PagerAdapter {

    /**
     * 上下文
     */
    private Activity mContext;
    /**
     * 布局加载器
     */
    private LayoutInflater mInflate;
    /**
     * 数据
     */
    private ArrayList<String> mData;
    /**
     * 异步图片加载工具
     */
    private DownloadImageLoader downloadImageLoader;

    public HotSalePagerAdapter(Activity context,ArrayList<String> list){
        mContext = context;
        mData = list;
        mInflate = LayoutInflater.from(mContext);
        downloadImageLoader = DownloadImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View rootView = mInflate.inflate(R.layout.item_banner,null);
        ImageView imageView = rootView.findViewById(R.id.iv_banner);
        if (position %3 == 0){
            downloadImageLoader
                    .loadImage(mContext
                            ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598529925750&di=" +
                                    "f1120431e7e086309e4040fd3a61862f&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.co" +
                                    "m%2Fq_70%2Cc_zoom%2Cw_640%2Fimages%2F20180809%2F77ab0f4c71e644ddb57448dac2d36713.jpg"
                            ,R.mipmap.ic_about
                            ,R.mipmap.ic_about
                            ,imageView);
        }else if (position % 3 == 1){
            downloadImageLoader
                    .loadImage(mContext
                            ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598529925750&di=05" +
                                    "6ed264b89ad79ce873e8a46c5c6737&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2" +
                                    "Fimages%2F20180204%2Fc42bc2c821724c86ad223ed4000d3da4.jpeg"
                            ,R.mipmap.ic_about
                            ,R.mipmap.ic_about
                            ,imageView);
        }else if (position %3 == 2){
            DownloadImageLoader.getInstance()
                    .loadImage(mContext
                            ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598530003007&di" +
                                    "=7c90c734c742aa50cbf2041b5e8eb730&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs." +
                                    "com%2Fimages%2F20181024%2F7a6eeefa8bb8431e8ebed8c8ddb4c6b5.jpeg"
                            ,R.mipmap.ic_about
                            ,R.mipmap.ic_about
                            ,imageView);
        }
        container.addView(rootView,0);
        return rootView;
    }
}
