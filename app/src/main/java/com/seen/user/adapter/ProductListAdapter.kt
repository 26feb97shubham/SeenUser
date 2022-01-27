package com.seen.user.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.ProductList
import kotlinx.android.synthetic.main.item_offers_discounts.view.*

class ProductListAdapter(
    private val context: Context,
    private val data: ArrayList<ProductList>,
    private val ref: String,
    private val clickInstance: ClickInterface.ClickPosTypeInterface
): RecyclerView.Adapter<ProductListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_offers_discounts, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (ref.equals("discount", true)){
            holder.itemView.price.visibility = View.VISIBLE
        }else{
            holder.itemView.price.visibility = View.GONE
        }

        holder.itemView.productName.text = data[position].name
        holder.itemView.category.text = data[position].category_name
        holder.itemView.supplierName.text = data[position].supplier_name
        holder.itemView.tv_ratings.text = data[position].rating.toString()
        holder.itemView.price.text = "AED "+ data[position].original_price
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

        Glide.with(context).load(data[position].supplier_profile_picture).placeholder(R.drawable.default_icon)
            .into(holder.itemView.supplierImg)

         holder.itemView.setOnClickListener {
             clickInstance.clickPostionType(position, "Click")
         }

        holder.itemView.ll_offer_discount_supplier.setOnClickListener {
            clickInstance.clickPostionType(position, "Supplier")
        }

//        val discounted_price = data[position].price.toFloat() - (data[position].price.toFloat() * data[position].discount.toFloat() / 100)
        holder.itemView.discountedprice.text = "AED " + data[position].price
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}