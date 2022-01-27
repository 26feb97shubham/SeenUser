package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.ProductList
import kotlinx.android.synthetic.main.item_order_products.view.*

class Orders_Products_Adapter(private val context: Context, private val data:ArrayList<ProductList>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<Orders_Products_Adapter.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
                LayoutInflater.from(context).inflate(R.layout.item_offers_discounts, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.productName.text = data[position].name
        holder.itemView.category.text = data[position].category_name
        holder.itemView.supplierName.text = data[position].supplier_name
        holder.itemView.price.text = "AED "+ data[position].price
        holder.itemView.discountPer.text = data[position].discount+"% OFF"
        Glide.with(context).load(data[position].files).placeholder(R.drawable.default_icon)
                .into(holder.itemView.img)

        holder.itemView.setOnClickListener {
//            clickInstance.clickPostion(position)
            clickInstance.clickPostion(position, "")
        }
    }

    override fun getItemCount(): Int {
        return  data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}