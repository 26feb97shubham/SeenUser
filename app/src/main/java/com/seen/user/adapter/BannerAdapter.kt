package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.model.Categories
import kotlinx.android.synthetic.main.item_banner.view.*

class BannerAdapter(private val context: Context, private val data:ArrayList<Categories>): RecyclerView.Adapter<BannerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        currentpos = position
        holder.itemView.txtTitle.text = data[position].name
       /* holder.itemView.txtDesc.text = data[position].desc*/
        Glide.with(context).load(data[position].image).placeholder(R.drawable.default_icon).into(holder.itemView.img)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object{
        var currentpos = 0
    }
}