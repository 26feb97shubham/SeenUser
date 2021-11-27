package com.dev.ecommerceuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.AccountTypesItem
import com.dev.ecommerceuser.model.CountriesItem
import kotlinx.android.synthetic.main.categories_name_list_layout.view.*

class AccountTypeAdapter(
        private val context : Context,
        private val accountTypeList : ArrayList<AccountTypesItem>,
        private val clickPositionInterface : ClickInterface.ClickPositionInterface
) : RecyclerView.Adapter<AccountTypeAdapter.AccountTypeAdapterVH>() {
    inner class AccountTypeAdapterVH(private val itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(accountTypesItem: AccountTypesItem, position: Int) {
            itemView.tv_category_name.text = accountTypesItem.name

            itemView.setOnClickListener {
                clickPositionInterface.clickPostion(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountTypeAdapterVH {
        val view= LayoutInflater.from(context).inflate(R.layout.categories_name_list_layout, parent, false)
        return AccountTypeAdapterVH(view)
    }

    override fun onBindViewHolder(holder: AccountTypeAdapterVH, position: Int) {
        val accountTypesItem = accountTypeList[position]
        holder.bind(accountTypesItem, position)
    }

    override fun getItemCount(): Int {
        return accountTypeList.size
    }
}