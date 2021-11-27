package com.dev.ecommerceuser.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.model.Cart
import kotlinx.android.synthetic.main.item_item_list.view.*

class ItemListAdapter(private val context: Context, private val data: ArrayList<Cart>): RecyclerView.Adapter<ItemListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_item_list, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(data[position].files).placeholder(R.drawable.default_icon).into(holder.itemView.img)
        holder.itemView.name.text=data[position].product_name
        holder.itemView.supplierName.text=data[position].supplier_name
        holder.itemView.category.text=data[position].category_name
        holder.itemView.price.text="AED "+data[position].price
        holder.itemView.stockCount.text=data[position].quantity.toString()+"pc"
    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}