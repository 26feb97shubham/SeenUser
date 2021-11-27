package com.dev.ecommerceuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.Categories
import com.dev.ecommerceuser.model.CategoriesItem
import kotlinx.android.synthetic.main.item_category.view.*

class SupplierCategoriesAdapter(private val context: Context,
                                private val data: ArrayList<CategoriesItem>) : RecyclerView.Adapter<SupplierCategoriesAdapter.SupplierCategoriesAdapterVH>() {
    inner class SupplierCategoriesAdapterVH(itemView : View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierCategoriesAdapterVH {
        val view= LayoutInflater.from(context).inflate(R.layout.supplier_categories_layout, parent, false)
        return SupplierCategoriesAdapterVH(view)

    }

    override fun onBindViewHolder(holder: SupplierCategoriesAdapterVH, position: Int) {
        holder.itemView.name.text=data[position].name
    }

    override fun getItemCount(): Int {
        return data.size
    }
}