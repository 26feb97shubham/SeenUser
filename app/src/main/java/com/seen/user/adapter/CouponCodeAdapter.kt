package com.seen.user.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Coupon
import kotlinx.android.synthetic.main.item_coupon_code.view.*


class CouponCodeAdapter(private val context: Context, private val data:ArrayList<Coupon>, private val  clickInstance: ClickInterface.ClickPosTypeInterface): RecyclerView.Adapter<CouponCodeAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_coupon_code, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.txtCouponName.text = data[position].title
        holder.itemView.txtCouponCode.text = data[position].code
        holder.itemView.txtDiscount.text = "Get "+ data[position].percentage + "% " +"upto AED "+data[position].max_discount_price
        holder.itemView.txtValidDesc.text = context.getString(R.string.valid_on_orders_with_items_worth_aed)+" "+data[position].min_price
        Glide.with(context).load(data[position].picture).placeholder(R.drawable.logo)
            .into(holder.itemView.imgCoupon)
        holder.itemView.btnApply.setOnClickListener {
            holder.itemView.btnApply.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostionType(position, "apply")
        }
        val underline = SpannableString(context.getString(R.string.terms_amp_conditions))
        underline.setSpan(UnderlineSpan(), 0, underline.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.itemView.txtTC.text=underline

        holder.itemView.txtTC.setOnClickListener {
            holder.itemView.txtTC.startAnimation(AlphaAnimation(1f, .5f))
            clickInstance.clickPostionType(position, "terms and conditions")
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}