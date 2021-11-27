package com.dev.ecommerceuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.MyLocations
import kotlinx.android.synthetic.main.item_my_location.view.*

class MyLocationsAdapter(private val context: Context, private val data:ArrayList<MyLocations>, private val clickInst:ClickInterface.ClickPosTypeInterface): RecyclerView.Adapter<MyLocationsAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_my_location, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
         holder.itemView.txtTitle.text = data[position].title
        holder.itemView.txtAddress.text = data[position].address
        if(data[position].set_as_default==1){
            holder.itemView.txtDefault.background = ContextCompat.getDrawable(context, R.drawable.gold_filled_rect_box)

        }
        else{
            holder.itemView.txtDefault.background = ContextCompat.getDrawable(context, R.drawable.gray_rect_box)
        }

        holder.itemView.txtEdit.setOnClickListener {
            holder.itemView.txtEdit.startAnimation(AlphaAnimation(1f, .5f))
            clickInst.clickPostionType(position, "Edit")
        }
        holder.itemView.txtDelete.setOnClickListener {
            holder.itemView.txtDelete.startAnimation(AlphaAnimation(1f, .5f))
            clickInst.clickPostionType(position, "Delete")
        }

        holder.itemView.txtDefault.setOnClickListener {
            if(data[position].set_as_default==0){
                holder.itemView.txtDefault.startAnimation(AlphaAnimation(1f, .5f))
                clickInst.clickPostionType(position, "Default")
            }

        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}