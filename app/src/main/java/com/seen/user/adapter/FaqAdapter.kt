package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seen.user.R
import com.seen.user.model.Faq
import kotlinx.android.synthetic.main.item_faq.view.*

class FaqAdapter(private val context: Context, private val data:ArrayList<Faq>): RecyclerView.Adapter<FaqAdapter.MyViewHolder>() {
    var selectedId:Int=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_faq, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.title.text = data[position].question
        holder.itemView.desc.text = data[position].answer
        if(position==selectedId){
            holder.itemView.imgDown.visibility= View.VISIBLE
            holder.itemView.imgRight.visibility= View.GONE
            holder.itemView.desc.visibility= View.VISIBLE
        }
        else{
            holder.itemView.imgDown.visibility= View.GONE
            holder.itemView.imgRight.visibility= View.VISIBLE
            holder.itemView.desc.visibility= View.GONE
        }
        holder.itemView.imgRight.setOnClickListener {
            selectedId=position
            notifyDataSetChanged()
        }
        holder.itemView.imgDown.setOnClickListener {
            holder.itemView.imgDown.visibility= View.GONE
            holder.itemView.imgRight.visibility= View.VISIBLE
            holder.itemView.desc.visibility= View.GONE
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}