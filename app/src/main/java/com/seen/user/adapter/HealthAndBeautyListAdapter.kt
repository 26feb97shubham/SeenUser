package com.seen.user.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.CategoryName
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.health_and_beauty_list_item.view.*
import kotlinx.android.synthetic.main.item_name_list.view.*
import kotlinx.android.synthetic.main.item_name_list.view.address
import kotlinx.android.synthetic.main.item_name_list.view.img
import kotlinx.android.synthetic.main.item_name_list.view.imgLike
import kotlinx.android.synthetic.main.item_name_list.view.name


class HealthAndBeautyListAdapter(private val context: Context, private val data:ArrayList<CategoryName>, private val clickInstance: ClickInterface.ClickPosTypeInterface)
    : RecyclerView.Adapter<HealthAndBeautyListAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var isLike:Boolean=false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.health_and_beauty_list_item, parent, false)
        return HealthAndBeautyListAdapter.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val requestOptions: RequestOptions =
            RequestOptions().error(R.drawable.default_icon).centerCrop()

        holder.itemView.name.text = data[position].name
        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].equals("ar")){
            holder.itemView.address.text = data[position].country_served_name_ar
        }else{
            holder.itemView.address.text = data[position].country_served_name
        }

        if(data[position].like){
            holder.isLike=true
            holder.itemView.imgLike.setImageResource(R.drawable.heart_red)
        }
        else{
            holder.isLike=false
            holder.itemView.imgLike.setImageResource(R.drawable.gray_heart)
        }
        holder.itemView.imgLike.setOnClickListener {
            holder.itemView.imgLike.startAnimation(AlphaAnimation(1f, 0.5f))
            clickInstance.clickPostionType(position, "Like")

        }
        Glide.with(context).load(data[position].profile_picture)
            .listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.healthandBeautyProductImageProgressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.healthandBeautyProductImageProgressBar.visibility = View.GONE
                    return false
                }

            })
            .apply(requestOptions)
            .into(holder.itemView.img)

        holder.itemView.setOnClickListener {
            holder.itemView.startAnimation(AlphaAnimation(1f, 0.5f))
            clickInstance.clickPostionType(position, "Click")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}