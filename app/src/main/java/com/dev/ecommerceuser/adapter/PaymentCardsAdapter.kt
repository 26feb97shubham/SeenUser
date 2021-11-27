package com.dev.ecommerceuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.Cards
import kotlinx.android.synthetic.main.item_payment_cards.view.*

class PaymentCardsAdapter(private val context: Context, private val data:ArrayList<Cards>, private val clickInst: ClickInterface.ClickPosInterface): RecyclerView.Adapter<PaymentCardsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_payment_cards, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.txtCard.text = data[position].card_number
        holder.itemView.radSelect.isChecked = data[position].set_as_default==1
        holder.itemView.radSelect.setOnClickListener {
//            clickInst.clickPostion(position)
            clickInst.clickPostion(position, "")
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}