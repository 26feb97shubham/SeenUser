package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.seen.user.R
import com.seen.user.model.DataItem
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_product_wise_search_layout.view.*

class BrandListAdapter(
    private val context: Context,
    private val brandsList: ArrayList<DataItem?>?,
    private val findNavController: NavController
) : RecyclerView.Adapter<BrandListAdapter.BrandListAdapterVH>() {
    inner class BrandListAdapterVH(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandListAdapterVH {
        val view= LayoutInflater.from(context).inflate(R.layout.item_product_wise_search_layout, parent, false)
        return BrandListAdapterVH(view)
    }

    override fun onBindViewHolder(holder: BrandListAdapterVH, position: Int) {
        val brandsItem = brandsList?.get(position)
        val lang = SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        if (brandsItem!=null){
            holder.itemView.product_name_mtv.text = brandsItem.name
        }

        holder.itemView.product_name_mtv.setOnClickListener {
            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.searchItem, holder.itemView.product_name_mtv.text.toString())
            findNavController.navigate(R.id.filteredproductsfragment)
        }
    }

    override fun getItemCount(): Int {
        return brandsList?.size!!
    }
}