package com.dev.ecommerceuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.Categories
import kotlinx.android.synthetic.main.item_large_categories.view.*
import kotlinx.android.synthetic.main.item_small_categories.view.*

class CategoriesAdapter(private val context: Context, private val data: ArrayList<Categories>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return if(viewType==0){
            val view=LayoutInflater.from(context).inflate(R.layout.item_large_categories, parent, false)
            MyViewHolder(view)
        } else{
            val view=LayoutInflater.from(context).inflate(R.layout.item_small_categories, parent, false)
            MyViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(holder.itemViewType==0) {
            Glide.with(context).load(data[position].image).placeholder(R.drawable.default_icon).into(holder.itemView.bigView)
            holder.itemView.txtBig.text=data[position].name

        }
        else{
            Glide.with(context).load(data[position].image).placeholder(R.drawable.default_icon).into(holder.itemView.smallView)
            holder.itemView.txtSmall.text=data[position].name

        }
        holder.itemView.setOnClickListener {
            clickInstance.clickPostion(position, "")
        }


    }
    override fun getItemViewType(position: Int): Int {
        var viewType = 1 //Default is 1
        if (position == 0) viewType = 0 //if zero, it will be a header view
        return viewType
    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
   /* class MyViewHolder2(itemView: View): RecyclerView.ViewHolder(itemView)*/
}