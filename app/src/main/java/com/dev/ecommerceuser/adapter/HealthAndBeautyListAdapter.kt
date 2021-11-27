package com.dev.ecommerceuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.CategoryName
import kotlinx.android.synthetic.main.item_name_list.view.*

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
        holder.itemView.name.text = data[position].name
        holder.itemView.address.text = data[position].country_served_name
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
        Glide.with(context).load(data[position].profile_picture).placeholder(R.drawable.default_icon)
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