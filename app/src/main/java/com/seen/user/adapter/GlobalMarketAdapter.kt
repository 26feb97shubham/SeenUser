package com.seen.user.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.GlobalMarkets
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_global_markets.view.*

class GlobalMarketAdapter(private val context: Context, private val data:ArrayList<GlobalMarkets>, private val clickInst:ClickInterface.ClickPosInterface): RecyclerView.Adapter<GlobalMarketAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_global_markets, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
            holder.itemView.flagName.text = data[position].country_name_ar
        }else{
            holder.itemView.flagName.text = data[position].country_name
        }

        Glide.with(context).load(data[position].image).placeholder(R.drawable.default_icon)
             .into(holder.itemView.flagImage)

         holder.itemView.setOnClickListener {
//             clickInst.clickPostion(position)
             clickInst.clickPostion(position, "")
         }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}