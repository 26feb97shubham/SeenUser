package com.seen.user.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
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
        final ImageView img = (ImageView)layout.findViewById(R.id.img);
        final TextView textTitle = (TextView)layout.findViewById(R.id.txtTitle);
        Glide.with(context).load(data.get(position).getImage()).placeholder(R.drawable.default_icon).into(img);
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
