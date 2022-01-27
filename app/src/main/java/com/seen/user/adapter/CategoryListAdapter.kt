package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Categories
import kotlinx.android.synthetic.main.item_category_list.view.*

class CategoryListAdapter(private val context: Context, private val data:ArrayList<Categories>, private val  clickInstance:ClickInterface.ClickPosInterface): RecyclerView.Adapter<CategoryListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_category_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.name.text = data[position].name
        Glide.with(context).load(data[position].image).placeholder(R.drawable.default_icon)
                .into(holder.itemView.img)
        holder.itemView.setOnClickListener {
            holder.itemView.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostion(position,"")

        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}