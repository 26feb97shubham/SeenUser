package com.dev.ecommerceuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecommerceuser.R
import kotlinx.android.synthetic.main.product_view_pager.view.*

class ProductImageAdapter(val context: Context, val productFiles: ArrayList<String>) : RecyclerView.Adapter<ProductImageAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.product_view_pager, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(productFiles.get(position)).placeholder(R.drawable.default_icon).into(holder.itemView.img)
    }

    override fun getItemCount(): Int {
        return productFiles.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}