package com.seen.user.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Cart
import kotlinx.android.synthetic.main.item_my_cart.view.*


class CartAdapter(private val context: Context, private val data:ArrayList<Cart>, private val clickIns:ClickInterface.ClickPosTypeInterface): RecyclerView.Adapter<CartAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_my_cart, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val requestOptions: RequestOptions =
            RequestOptions().error(R.drawable.default_icon).centerCrop()

        holder.itemView.productName.text = data[position].product_name
        holder.itemView.supplierName.text = data[position].supplier_name
        holder.itemView.gadgets.text = data[position].category_name
        holder.itemView.txtQty.text = data[position].quantity.toString()
        holder.itemView.price.text = "AED "+ data[position].price
        holder.itemView.discountPer.text = data[position].discount+"% OFF"

        val productImage = if (data[position].files.isEmpty()){
            context.getDrawable(R.drawable.default_icon).toString()
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
                    holder.itemView.cartProductImageProgressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.cartProductImageProgressBar.visibility = View.GONE
                    return false
                }

            })
            .apply(requestOptions).into(holder.itemView.img)
        Glide.with(context).load(data[position].supplier_image).placeholder(R.drawable.default_icon).into(holder.itemView.supplierImg)

        if(data[position].like){
            Glide.with(context).load(R.drawable.heart_red).into(holder.itemView.imgLike)
        }
        else{
            Glide.with(context).load(R.drawable.heart_white).into(holder.itemView.imgLike)
        }

        if(data[position].product_available_status){
            holder.itemView.notAvailTxt.visibility=View.GONE
            holder.itemView.notAvailTxt.text=data[position].product_available_message
        }
        else{
            holder.itemView.notAvailTxt.visibility=View.VISIBLE
        }
        holder.itemView.setOnClickListener {
            holder.itemView.startAnimation(AlphaAnimation(1f, .5f))
            clickIns.clickPostionType(position, "Details")
        }
        holder.itemView.remove.setOnClickListener {
            holder.itemView.remove.startAnimation(AlphaAnimation(1f, .5f))
            clickIns.clickPostionType(position, "Minus")
        }
        holder.itemView.add.setOnClickListener {
            holder.itemView.add.startAnimation(AlphaAnimation(1f, .5f))
            clickIns.clickPostionType(position, "Plus")
        }
        holder.itemView.cross.setOnClickListener {
            holder.itemView.cross.startAnimation(AlphaAnimation(1f, .5f))
            clickIns.clickPostionType(position, "Delete")
        }

        holder.itemView.ll_supplier_layout.setOnClickListener {
            holder.itemView.ll_supplier_layout.startAnimation(AlphaAnimation(1f, .5f))
            clickIns.clickPostionType(position, "Supplier")
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}