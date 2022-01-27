package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.seen.user.R
import com.seen.user.model.ProductsItemXX
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_product_wise_search_layout.view.*

class ProductWiseListAdapter(
    private val context: Context,
    private val productWiseList: ArrayList<ProductsItemXX?>?,
    private val findNavController: NavController
) :
    RecyclerView.Adapter<ProductWiseListAdapter.ProductWiseListAdapterVH>() {
    inner class ProductWiseListAdapterVH(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductWiseListAdapterVH {
        val view= LayoutInflater.from(context).inflate(R.layout.item_product_wise_search_layout, parent, false)
        return ProductWiseListAdapterVH(view)
    }

    override fun onBindViewHolder(holder: ProductWiseListAdapterVH, position: Int) {
        val productWiseItem = productWiseList?.get(position)

        if (productWiseItem != null) {
            holder.itemView.product_name_mtv.text = productWiseItem.name
        }

        holder.itemView.product_name_mtv.setOnClickListener {
            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.searchItem, holder.itemView.product_name_mtv.text.toString())
            findNavController.navigate(R.id.filteredproductsfragment)
        }

    }

    override fun getItemCount(): Int {
        if (productWiseList != null) {
            return productWiseList.size
        }else{
            return 0
        }
    }
}