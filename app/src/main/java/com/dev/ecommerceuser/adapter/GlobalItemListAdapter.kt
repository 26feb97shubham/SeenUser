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
import com.dev.ecommerceuser.model.Supplier
import kotlinx.android.synthetic.main.item_global_list.view.*
import java.util.*

class GlobalItemListAdapter(private val context: Context, private val data:ArrayList<Supplier>, private val clickInst:ClickInterface.ClickPosTypeInterface): RecyclerView.Adapter<GlobalItemListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_global_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.productName.text = data[position].name
        holder.itemView.category.text = data[position].categories
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
                .into(holder.itemView.img)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}