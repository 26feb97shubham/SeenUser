package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.HomeCategories
import kotlinx.android.synthetic.main.item_home_categories.view.*

class HomeCategoriesAdapter(private val context:Context, private val data:ArrayList<HomeCategories>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<HomeCategoriesAdapter.MyViewHolder>() {

    var selectPos:Int=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.item_home_categories, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(selectPos==position){
            holder.itemView.iconView.setBackgroundResource(R.drawable.gold_filled_small_radius_box_1)
            holder.itemView.img.setColorFilter(ContextCompat.getColor(context, R.color.white))
        }
        else{
            holder.itemView.iconView.setBackgroundResource(R.drawable.gray_curved_rect_box)
            holder.itemView.img.setColorFilter(ContextCompat.getColor(context, R.color.primary_dark_gray))
        }
        holder.itemView.txtCatName.text=data[position].name
        Glide.with(context).load(data[position].icon).placeholder(R.drawable.ic_launcher_foreground).into(holder.itemView.img)

        holder.itemView.setOnClickListener {
           /* clickInstance.clickPostion(position)*/
            clickInstance.clickPostion(position, "")
            selectPos=position
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
       return data.size
    }
    class MyViewHolder(itemView:View): RecyclerView.ViewHolder(itemView)
}


