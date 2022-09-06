package com.hao.albumlib.album.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.hao.albumlib.R;
import com.hao.albumlib.album.activity.AlbumActivity;
import com.hao.albumlib.album.utils.ImageLoader;
import com.hao.baselib.utils.DpUtil;
import com.hao.baselib.utils.ScreenUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageAdapter extends BaseAdapter {

    private String mDirPath;
    private List<String> mImgPaths;
    private LayoutInflater mInflater;
    private AlbumActivity albumActivity;
    private boolean isMutil;//是否是多张


    public ImageAdapter(Activity activity, List<String> mDatas, String dirPath){
        albumActivity = (AlbumActivity) activity;
        this.mDirPath = dirPath;
        this.mImgPaths = mDatas;
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return mImgPaths.size();
    }

    @Override
    public Object getItem(int position) {
        return mImgPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.item_album,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.mImg = convertView.findViewById(R.id.id_item_image);
            viewHolder.mSelect = convertView.findViewById(R.id.id_item_select);
            //这里要根据手机尺寸修改一下图片的大小
            int widthAndHeight = (ScreenUtil.getScreenWidth(albumActivity) - DpUtil.dp2px(albumActivity,9))/4;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.mImg.getLayoutParams();
            params.width = widthAndHeight;
            params.height = widthAndHeight;
            viewHolder.mImg.setLayoutParams(params);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //重置状态
        viewHolder.mImg.setImageResource(R.mipmap.no_image);
        viewHolder.mSelect.setImageResource(R.mipmap.ic_unchoose);
        viewHolder.mImg.setColorFilter(null);
        if (!isMutil){
            viewHolder.mSelect.setVisibility(View.GONE);
        }
        ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(mDirPath+"/"+mImgPaths.get(position),viewHolder.mImg);
        final String filePath = mDirPath+"/"+mImgPaths.get(position);
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMutil) {
                    //多选的处理
                    if (albumActivity.getPicList().contains(filePath)) {
                        //已经被选择
                        albumActivity.removePicFromList(filePath);
                        finalViewHolder.mImg.setColorFilter(null);
                        finalViewHolder.mSelect.setImageResource(R.mipmap.ic_unchoose);
                    } else {
                        //未被选择
                        albumActivity.setPicToList(filePath);
                        finalViewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
                        finalViewHolder.mSelect.setImageResource(R.mipmap.ic_choose);
                    }
                }else{
                    //单选的处理
                    albumActivity.singleGet(filePath);
                }
            }
        });
        if (albumActivity.getPicList().contains(filePath)){
            viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
            viewHolder.mSelect.setImageResource(R.mipmap.ic_choose);
        }
        return convertView;
    }

    private class ViewHolder{
        ImageView mImg;
        ImageButton mSelect;
    }

    /**
     * 设置是否多张
     * @param mutil
     */
    public void setMutil(boolean mutil) {
        isMutil = mutil;
    }
}
