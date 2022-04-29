package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seen.user.R
import com.seen.user.model.CategoriesItem
import com.seen.user.model.CategoriesItemX
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_category.view.*

class SupplierCategoriesAdapter(private val context: Context,
                                private val data: ArrayList<CategoriesItemX>) : RecyclerView.Adapter<SupplierCategoriesAdapter.SupplierCategoriesAdapterVH>() {
    inner class SupplierCategoriesAdapterVH(itemView : View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierCategoriesAdapterVH {
        val view= LayoutInflater.from(context).inflate(R.layout.supplier_categories_layout, parent, false)
        return SupplierCategoriesAdapterVH(view)

    }

    override fun onBindViewHolder(holder: SupplierCategoriesAdapterVH, position: Int) {
        if (SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].equals("ar")){
            holder.itemView.name.text=data[position].nameAr
        }else{
            holder.itemView.name.text=data[position].name
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}