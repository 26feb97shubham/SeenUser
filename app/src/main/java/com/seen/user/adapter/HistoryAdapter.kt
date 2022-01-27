package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.MyOrders
import com.seen.user.model.ProductList
import kotlinx.android.synthetic.main.fragment_favourites.view.rvList
import kotlinx.android.synthetic.main.item_history_orders.view.*

class HistoryAdapter(private val context: Context, private val data:ArrayList<MyOrders>, private val clickInst: ClickInterface.ClickPosInterface): RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {
    var order_status: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_history_orders, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.orderNum.text = context.getString(R.string.order_hess)+data[position].order_id
        holder.itemView.orderNumCard.text = context.getString(R.string.order_hess)+data[position].order_id
        holder.itemView.deliveryDate.text = context.getString(R.string.date_delivered_colon)+"  "+data[position].delivery_date
        order_status = data[position].status

        /*Glide.with(context).load(data[position].file).placeholder(R.drawable.default_icon)
                .into(holder.itemView.img)*/

         holder.itemView.btnViewOrder.setOnClickListener {
             holder.itemView.btnViewOrder.startAnimation(AlphaAnimation(1f, .5f))
//             clickInst.clickPostion(position)
             clickInst.clickPostion(position, "")
         }

        holder.itemView.layout_right_bottom.setOnClickListener {
            holder.itemView.layout_right_bottom.startAnimation(AlphaAnimation(1f, .5f))
            if(holder.isOpen){
                holder.isOpen=false
                holder.itemView.imgRightArrow.visibility=View.VISIBLE
                holder.itemView.imgDownArrow.visibility=View.GONE
//                holder.itemView.img.visibility=View.VISIBLE
                holder.itemView.orderView.visibility=View.GONE

            }
            else{
                holder.isOpen=true
                holder.itemView.imgRightArrow.visibility=View.GONE
                holder.itemView.imgDownArrow.visibility=View.VISIBLE
/*                holder.itemView.img.visibility=View.GONE*/
                holder.itemView.orderView.visibility=View.VISIBLE
            }

        }
        holder.productList.clear()
        for(i in 0 until data[position].order_data.length()){
            val obj1 = data[position].order_data.getJSONObject(i)
            val p = ProductList()
            p.quantity = obj1.getInt("quantity")
            p.id = obj1.getInt("product_id")
            p.price = obj1.getString("price")
            p.discount = obj1.getString("discount")
            p.product_item_id = obj1.getString("product_item_id")
            p.category_name = obj1.getString("category_name")
            p.name = obj1.getString("product_name")
            p.supplier_name = obj1.getString("supplier_name")
            p.files = obj1.getString("files")
            holder.productList.add(p)
        }

        holder.itemView.rvList.layoutManager= LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.ordersProductAdapter = OrdersProductAdapter(context,holder.productList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int, type : String) {
            }
        })
        holder.itemView.rvList.adapter= holder.ordersProductAdapter

    }

    override fun getItemCount(): Int {
        return  data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var isOpen=false
        var productList=ArrayList<ProductList>()
        lateinit var ordersProductAdapter: OrdersProductAdapter
    }
}