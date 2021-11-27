package com.dev.ecommerceuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.MyOrders
import com.dev.ecommerceuser.model.ProductList
import kotlinx.android.synthetic.main.fragment_favourites.view.rvList
import kotlinx.android.synthetic.main.fragment_order_status.view.*
import kotlinx.android.synthetic.main.item_upcoming.view.*
import kotlinx.android.synthetic.main.item_upcoming.view.deliveryDate
import kotlinx.android.synthetic.main.item_upcoming.view.orderNum


class UpComingAdapter(private val context: Context, private val data:ArrayList<MyOrders>, private val clickInst: ClickInterface.ClickPosInterface): RecyclerView.Adapter<UpComingAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_upcoming, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.orderNum.text = context.getString(R.string.order_hess)+data[position].order_id
        holder.itemView.deliveryDate.text = context.getString(R.string.delivery_date)+"  "+data[position].delivery_date
        order_status = data[position].status

        holder.itemView.btnChkStatus.setOnClickListener {
            holder.itemView.btnChkStatus.startAnimation(AlphaAnimation(1f, .5f))
//            clickInst.clickPostion(position)
            clickInst.clickPostion(position, "")
        }

         holder.productList.clear()
         for(i in 0 until data[position].order_data.length()){
             val obj1 = data[position].order_data.getJSONObject(i)
             val p = ProductList()
             p.quantity = obj1.getInt("quantity")
             p.id = obj1.getInt("product_id")
             p.price = obj1.getString("price")
             p.discount = obj1.getString("discount")
           /*  p.product_item_id = obj1.getInt("product_item_id")*/
             p.category_name = obj1.getString("category_name")
             p.name = obj1.getString("product_name")
             p.supplier_name = obj1.getString("supplier_name")
             p.files = obj1.getString("files")
             holder.productList.add(p)
         }

       /* holder.ordersProductAdapter= OrdersProductAdapter(context,  holder.productList, object :ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {
            }

        })*/
        holder.itemView.rvList.layoutManager= LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.ordersProductAdapter = OrdersProductAdapter(context,holder.productList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int, type : String) {
            }
        })
        holder.itemView.rvList.adapter= holder.ordersProductAdapter
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var productList=ArrayList<ProductList>()
        lateinit var ordersProductAdapter: OrdersProductAdapter

    }
    companion object
    {
        var order_status: Int = 0
    }
}