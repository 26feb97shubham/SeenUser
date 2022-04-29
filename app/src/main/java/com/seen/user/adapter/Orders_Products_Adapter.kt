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
import kotlinx.android.synthetic.main.item_offers_discounts.view.*
import kotlinx.android.synthetic.main.item_order_products.view.*
import kotlinx.android.synthetic.main.item_order_products.view.category
import kotlinx.android.synthetic.main.item_order_products.view.discountPer
import kotlinx.android.synthetic.main.item_order_products.view.img
import kotlinx.android.synthetic.main.item_order_products.view.price
import kotlinx.android.synthetic.main.item_order_products.view.productName
import kotlinx.android.synthetic.main.item_order_products.view.supplierName


class Orders_Products_Adapter(private val context: Context, private val data:ArrayList<ProductList>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<Orders_Products_Adapter.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
                LayoutInflater.from(context).inflate(R.layout.item_offers_discounts, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val requestOptions: RequestOptions =
            RequestOptions().error(R.drawable.def_product).centerCrop()

        holder.itemView.productName.text = data[position].name
        holder.itemView.category.text = data[position].category_name
        holder.itemView.supplierName.text = data[position].supplier_name
        holder.itemView.price.text = "AED "+ data[position].price
        holder.itemView.discountPer.text = data[position].discount+"% OFF"
        val productImage = if(data[position].files.isEmpty()){
            context.getDrawable(R.drawable.def_product).toString()
        }else{
            data[position].files
        }
        Glide.with(context).load(data[position].files)
            .listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.itemofferdiscountProductImageProgressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.itemofferdiscountProductImageProgressBar.visibility = View.GONE
                    return false
                }

            })
            .apply(requestOptions)
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