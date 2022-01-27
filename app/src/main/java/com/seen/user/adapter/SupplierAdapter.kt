package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.SuppliersItem
import kotlinx.android.synthetic.main.categories_name_list_layout.view.*

class SupplierAdapter(private val context : Context,
                      private val suppliersList : ArrayList<SuppliersItem?>?,
                      private val clickPositionInterface : ClickInterface.ClickPositionInterface) : RecyclerView.Adapter<SupplierAdapter.SupplierAdapterVH>() {
    inner class SupplierAdapterVH(private val itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(accountTypesItem: SuppliersItem, position: Int) {
            itemView.tv_category_name.text = accountTypesItem.name

            itemView.setOnClickListener {
                clickPositionInterface.clickPostion(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierAdapterVH {
        val view= LayoutInflater.from(context).inflate(R.layout.categories_name_list_layout, parent, false)
        return SupplierAdapterVH(view)
    }

    override fun onBindViewHolder(holder: SupplierAdapterVH, position: Int) {
        val accountTypesItem = suppliersList?.get(position)
        if (accountTypesItem != null) {
            holder.bind(accountTypesItem, position)
        }
    }

    override fun getItemCount(): Int {
        return suppliersList!!.size
    }
}