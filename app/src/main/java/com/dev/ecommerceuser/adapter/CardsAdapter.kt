package com.dev.ecommerceuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.Cards
import kotlinx.android.synthetic.main.item_cards.view.*


class CardsAdapter(private val context: Context, private val data:ArrayList<Cards>, private val clickInst:ClickInterface.ClickPosTypeInterface): RecyclerView.Adapter<CardsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cards, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.edtCard.setText(data[position].card_number)
        holder.itemView.radSelect.isChecked = data[position].set_as_default==1
        holder.itemView.edit.setOnClickListener {
            holder.itemView.edit.startAnimation(AlphaAnimation(1f, .5f))
            clickInst.clickPostionType(position, "2")
        }
        holder.itemView.delete.setOnClickListener {
            holder.itemView.delete.startAnimation(AlphaAnimation(1f, .5f))
            clickInst.clickPostionType(position, "3")
        }

        holder.itemView.radSelect.isEnabled=false
        holder.itemView.cardCount.text= "Card "+position+1


    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}