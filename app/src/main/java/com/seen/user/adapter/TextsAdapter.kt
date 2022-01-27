package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import kotlinx.android.synthetic.main.item_texts.view.*


class TextsAdapter(private val context: Context, private val data: ArrayList<String>, private val adapterNum:Int, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<TextsAdapter.MyViewHolder>() {
    var selectedPos=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.item_texts, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.name.text=data[position]

      /*  when (adapterNum) {
            1 -> {
                selectedPos=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.PrimaryAdapterPos, 0]
            }
            2 -> {
                selectedPos=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SecondaryAdapterPos, 0]
            }
            3 -> {
                selectedPos=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.TertiaryAdapterPos, 0]
            }
        }*/

        if(selectedPos==position){
            holder.itemView.name.isSelected = true
//            clickInstance.clickPostion(position)
            clickInstance.clickPostion(position, "")
        }
        else{
            holder.itemView.name.isSelected = false
        }

        holder.itemView.setOnClickListener {
            if(selectedPos != position) {
                selectedPos = position
            /*    when (adapterNum) {
                    1 -> {
                        SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.PrimaryAdapterPos, selectedPos)
                    }
                    2 -> {
                        SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.SecondaryAdapterPos, selectedPos)
                    }
                    3 -> {
                        SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.TertiaryAdapterPos, selectedPos)
                    }
                    //                clickInstance.clickPostion(position)
                }*/
                notifyDataSetChanged()
//                clickInstance.clickPostion(position)
            }
//            clickInstance.clickPostion(position)
           /* if (SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.AdapterClickCount, 0] >= adapterNum - 1) {
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.AdapterClickCount, adapterNum)
                selectedPos=position
                notifyDataSetChanged()
                clickInstance.clickPostion(position)
            }*/
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}