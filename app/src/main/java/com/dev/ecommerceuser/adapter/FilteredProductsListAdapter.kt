package com.dev.ecommerceuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.utils.LogUtils
import kotlinx.android.synthetic.main.item_recent_products.view.*

class FilteredProductsListAdapter(private val context: Context) :
    RecyclerView.Adapter<FilteredProductsListAdapter.FilteredProductsListAdapterVH>() {
    inner class FilteredProductsListAdapterVH(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilteredProductsListAdapterVH {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recent_products, parent, false)
        return FilteredProductsListAdapterVH(view)
    }

    override fun onBindViewHolder(holder: FilteredProductsListAdapterVH, position: Int) {
        holder.itemView.iv_add_to_cart.setOnClickListener {
            LogUtils.shortCenterToast(context, "Item added to cart successfully!!!!. '\n' Thanks alot for purchasing")
        }

    }

    override fun getItemCount(): Int {
        return 30
    }
}