package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Cards
import kotlinx.android.synthetic.main.item_cards.view.*


class CardsAdapter(private val context: Context, private val data:ArrayList<Cards>, private val clickInst:ClickInterface.ClickPosTypeInterface): RecyclerView.Adapter<CardsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cards, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val card_number = "**** **** **** "+data[position].card_number
        holder.itemView.edtCard.setText(card_number)
        if (data[position].set_as_default==1){
            holder.itemView.radSelect.isChecked = true
        }else{
            holder.itemView.radSelect.isChecked = false
        }
      /*  holder.itemView.radSelect.isChecked = data[position].set_as_default==1*/
        holder.itemView.edit.setOnClickListener {
            holder.itemView.edit.startAnimation(AlphaAnimation(1f, .5f))
            clickInst.clickPostionType(position, "2")
        }
        holder.itemView.delete.setOnClickListener {
            holder.itemView.delete.startAnimation(AlphaAnimation(1f, .5f))
            clickInst.clickPostionType(position, "3")
        }

        holder.itemView.radSelect.isEnabled=false
        val position_1  = position+1
        holder.itemView.cardCount.text= "Card "+position_1


    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}