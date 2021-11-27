package com.dev.ecommerceuser.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.ProductList
import kotlinx.android.synthetic.main.item_offers_discounts.view.*

class ProductListAdapter(private val context: Context, private val data:ArrayList<ProductList>, private val clickInstance:ClickInterface.ClickPosTypeInterface): RecyclerView.Adapter<ProductListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_offers_discounts, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.productName.text = data[position].name
        holder.itemView.category.text = data[position].category_name
        holder.itemView.supplierName.text = data[position].supplier_name
        holder.itemView.tv_ratings.text = data[position].rating.toString()
        holder.itemView.price.text = "AED "+ data[position].price
        holder.itemView.price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        holder.itemView.discountPer.text = data[position].discount+"% OFF"
        if(data[position].like){
            holder.itemView.imgLike.setImageResource(R.drawable.heart_red)
        }
        else{
            holder.itemView.imgLike.setImageResource(R.drawable.heart_white)
        }
        holder.itemView.imgLike.setOnClickListener {
            clickInstance.clickPostionType(position, "Like")
        }
         Glide.with(context).load(data[position].files).placeholder(R.drawable.default_icon)
             .into(holder.itemView.img)

         holder.itemView.setOnClickListener {
             clickInstance.clickPostionType(position, "Click")
         }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}