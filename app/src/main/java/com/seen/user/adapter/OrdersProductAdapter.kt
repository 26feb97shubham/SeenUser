package com.seen.user.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.ProductList
import kotlinx.android.synthetic.main.item_order_products.view.*
import kotlinx.android.synthetic.main.item_order_products.view.category
import kotlinx.android.synthetic.main.item_order_products.view.discountPer
import kotlinx.android.synthetic.main.item_order_products.view.img
import kotlinx.android.synthetic.main.item_order_products.view.price
import kotlinx.android.synthetic.main.item_order_products.view.productName
import kotlinx.android.synthetic.main.item_order_products.view.supplierName
import kotlinx.android.synthetic.main.order_product_item.view.*


class OrdersProductAdapter(private val context: Context, private val data:ArrayList<ProductList>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<OrdersProductAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.order_product_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val requestOptions: RequestOptions =
            RequestOptions().error(R.drawable.def_product).centerCrop()
        val requestOptions1: RequestOptions =
            RequestOptions().error(R.drawable.user).centerCrop()
        holder.itemView.productName.text = data[position].name
        holder.itemView.category.text = data[position].category_name
        holder.itemView.supplierName.text = data[position].supplier_name
        holder.itemView.discountedprice.text = "AED "+ data[position].price
        holder.itemView.discountPer.text = data[position].discount+"% OFF"
        val productImage = if (data[position].files.isEmpty()){
            context.getDrawable(R.drawable.def_product).toString()
        }else{
            data[position].files
        }
        Glide.with(context).load(productImage)
            .listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.orderProductProgressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.orderProductProgressBar.visibility = View.GONE
                    return false
                }

            })
            .apply(requestOptions)
            .into(holder.itemView.img)
        val supplierImage = if (data[position].supplier_profile_picture.isEmpty()){
            context.getDrawable(R.drawable.user).toString()
        }else{
            data[position].supplier_profile_picture
        }
        Glide.with(context).load(supplierImage).apply(requestOptions1).into(holder.itemView.supplierImg)

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