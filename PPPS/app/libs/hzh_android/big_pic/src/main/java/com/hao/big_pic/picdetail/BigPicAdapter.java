package com.hao.big_pic.picdetail;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;
import com.hao.big_pic.R;
import com.hao.imageloadlib.loadimage.GlideImageLoader;
import java.util.List;
import uk.co.senab.photoview.PhotoView;

/**
 * 大图预览适配器
 * @author WaterWood
 */
public class BigPicAdapter extends PagerAdapter {

    private Context context;
    private List<String> listPic;

    public BigPicAdapter(Context context, List<String> listPic) {
        this.context = context;
        this.listPic = listPic;
    }

    @Override
    public int getCount() {
        return listPic.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new PhotoView(context);
        GlideImageLoader.getInstance().loadImage(context,listPic.get(position),R.mipmap.ic_seize,R.mipmap.ic_error,imageView);
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return imageView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
