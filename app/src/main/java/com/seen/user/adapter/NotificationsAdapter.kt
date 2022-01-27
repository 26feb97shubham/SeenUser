package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Notifications
import kotlinx.android.synthetic.main.item_notifications_list.view.*

class NotificationsAdapter(private val context: Context, private val data:ArrayList<Notifications>, private val clickInst: ClickInterface.ClickPosInterface): RecyclerView.Adapter<NotificationsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_notifications_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.txtTitle.text = data[position].title
        holder.itemView.txtDetails.text = data[position].message
        holder.itemView.txtTime.text = data[position].time
        if(data[position].seen==0){
            holder.itemView.imgNotWell.setImageResource(R.drawable.not_well_01)
        }
        else{
            holder.itemView.imgNotWell.setImageResource(R.drawable.not05)
        }

        holder.itemView.setOnClickListener {
            holder.itemView.startAnimation(AlphaAnimation(1f, .5f))
//            clickInst.clickPostion(position)
            clickInst.clickPostion(position, "")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}