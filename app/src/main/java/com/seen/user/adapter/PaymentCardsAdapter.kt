package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Cards
import kotlinx.android.synthetic.main.item_payment_cards.view.*

class PaymentCardsAdapter(private val context: Context, private val data:ArrayList<Cards>, private val clickInst: ClickInterface.ClickPosInterface): RecyclerView.Adapter<PaymentCardsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_payment_cards, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pos = position+1
        val card = context.getString(R.string.card)+" "+pos+" ( "+context.getString(R.string.card_ending_in)+" "+data[position].card_number+" )"
        holder.itemView.txtCard.text = card
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