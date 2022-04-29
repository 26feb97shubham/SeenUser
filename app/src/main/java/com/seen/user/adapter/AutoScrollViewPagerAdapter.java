package com.seen.user.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.seen.user.R;
import com.seen.user.model.Categories;

import java.util.ArrayList;

public class AutoScrollViewPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Categories> data;

    public AutoScrollViewPagerAdapter(Context context, ArrayList<Categories> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return  view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup parent, int position) {
        View layout = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        assert layout!= null;
        RequestOptions requestOptions = new RequestOptions().error(R.drawable.default_icon).centerCrop();
        final ImageView img = (ImageView)layout.findViewById(R.id.img);
        final TextView textTitle = (TextView)layout.findViewById(R.id.txtTitle);
        final ProgressBar bannerImageProgressBar = (ProgressBar)layout.findViewById(R.id.bannerImageProgressBar);
        String bannerImage = "";
        if(data.get(position).getImage().isEmpty()){
            bannerImage = context.getDrawable(R.drawable.default_icon).toString();
        }else{
            bannerImage = data.get(position).getImage();
        }
        Glide.with(context).load(bannerImage)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        bannerImageProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        bannerImageProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(requestOptions).into(img);
        textTitle.setText(data.get(position).getName());

        parent.addView(layout,0);

        return layout;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
