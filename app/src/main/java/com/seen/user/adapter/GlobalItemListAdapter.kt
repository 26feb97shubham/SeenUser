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
import com.bumptech.glide.request.target.Target
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Supplier
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_global_list.view.*
import java.util.*

class GlobalItemListAdapter(private val context: Context, private val data:ArrayList<Supplier>, private val clickInst:ClickInterface.ClickPosTypeInterface): RecyclerView.Adapter<GlobalItemListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_global_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].equals("ar")){
            holder.itemView.supp_category.text = data[position].categories_ar
        }else{
            holder.itemView.supp_category.text = data[position].categories
        }
        holder.itemView.supplier_name.text = data[position].name

        holder.itemView.tv_ratings.text = data[position].rating.toString()
        if(data[position].like){
            holder.itemView.imgLike_global_market.setImageResource(R.drawable.heart_red)
        }
        else{
            holder.itemView.imgLike_global_market.setImageResource(R.drawable.heart_white)
        }
        holder.itemView.imgLike_global_market.setOnClickListener {
            holder.itemView.imgLike_global_market.startAnimation(AlphaAnimation(1f, 0.5f))
            clickInst.clickPostionType(position, "Like")
        }
        holder.itemView.setOnClickListener {
            holder.itemView.startAnimation(AlphaAnimation(1f, 0.5f))
            clickInst.clickPostionType(position, "Click")
        }
        Glide.with(context).load(data[position].profile_picture).placeholder(R.drawable.default_icon)
            .listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.globalItemProductImageProgressbar.visibility= View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.globalItemProductImageProgressbar.visibility= View.GONE
                    return false
                }

            })
                .into(holder.itemView.img)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}