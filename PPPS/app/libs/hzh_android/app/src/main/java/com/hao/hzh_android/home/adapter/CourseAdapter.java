package com.hao.hzh_android.home.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.hao.hzh_android.R;
import com.hao.imageloadbydown.imageload.DownloadImageLoader;
import java.util.ArrayList;

/**
 * 多布局适配器
 * @author WaterWood
 */
public class CourseAdapter extends BaseAdapter {

    /**
     * 类型数量
     */
    private static final int CARD_COUNT = 4;
    /**
     * 单图类型item
     */
    private static final int CARD_SIGLE_PIC = 0x01;
    /**
     * 多图类型item
     */
    private static final int CARD_MULTI_PIC = 0x02;
    /**
     * 上下文
     */
    private Activity mContext;
    /**
     * 布局缓存
     */
    private ViewHolder mViewHolder;
    /**
     * Layout加载器
     */
    private LayoutInflater mInflate;
    /**
     * 数据,这里的泛型要根据实际数据类型放置
     */
    private ArrayList<String> mData;
    /**
     * 异步图片加载工具
     */
    private DownloadImageLoader downloadImageLoader;

    public CourseAdapter(Activity activity, ArrayList<String> data) {
        mContext = activity;
        mData = data;
        mInflate = LayoutInflater.from(mContext);
        downloadImageLoader = DownloadImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return CARD_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0){
            return CARD_SIGLE_PIC;
        }else{
            return CARD_MULTI_PIC;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null){
            switch (type){
                case CARD_SIGLE_PIC:
                    mViewHolder = new ViewHolder();
                    convertView = mInflate.inflate(R.layout.item_mutil_single,parent,false);
                    mViewHolder.iv_single = convertView.findViewById(R.id.iv_single);
                    break;
                case CARD_MULTI_PIC:
                    mViewHolder = new ViewHolder();
                    convertView = mInflate.inflate(R.layout.item_mutil_three,parent,false);
                    mViewHolder.iv_single1 = convertView.findViewById(R.id.iv_single1);
                    mViewHolder.iv_single2 = convertView.findViewById(R.id.iv_single2);
                    mViewHolder.iv_single3 = convertView.findViewById(R.id.iv_single3);
                    break;
            }
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        switch (type){
            case CARD_SIGLE_PIC:
                mViewHolder.iv_single.setImageResource(R.mipmap.aaaaa);
                break;
            case CARD_MULTI_PIC:
                downloadImageLoader
                    .loadImage(mContext
                            ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598529925750&di=" +
                                    "f1120431e7e086309e4040fd3a61862f&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.co" +
                                    "m%2Fq_70%2Cc_zoom%2Cw_640%2Fimages%2F20180809%2F77ab0f4c71e644ddb57448dac2d36713.jpg"
                            ,R.mipmap.ic_about
                            ,R.mipmap.ic_about
                            ,mViewHolder.iv_single1);
                downloadImageLoader
                        .loadImage(mContext
                                ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598529925750&di=05" +
                                        "6ed264b89ad79ce873e8a46c5c6737&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2" +
                                        "Fimages%2F20180204%2Fc42bc2c821724c86ad223ed4000d3da4.jpeg"
                                ,R.mipmap.ic_about
                                ,R.mipmap.ic_about
                                ,mViewHolder.iv_single2);
                DownloadImageLoader.getInstance()
                        .loadImage(mContext
                                ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598530003007&di" +
                                        "=7c90c734c742aa50cbf2041b5e8eb730&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs." +
                                        "com%2Fimages%2F20181024%2F7a6eeefa8bb8431e8ebed8c8ddb4c6b5.jpeg"
                                ,R.mipmap.ic_about
                                ,R.mipmap.ic_about
                                ,mViewHolder.iv_single3);
                break;
        }
        return convertView;
    }

    private class ViewHolder{
        private ImageView iv_single;
        private ImageView iv_single1;
        private ImageView iv_single2;
        private ImageView iv_single3;
    }
}
