package com.dev.ecommerceuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.Products
import kotlinx.android.synthetic.main.item_product.view.*

class ProductAdapter(private val context: Context, private val data: ArrayList<Products>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(data[position].files).placeholder(R.drawable.default_icon).into(holder.itemView.img)
        holder.itemView.name.text=data[position].name
        holder.itemView.setOnClickListener {
//            clickInstance.clickPostion(position)
            clickInstance.clickPostion(position, "")
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}