package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.CountriesItem
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.categories_name_list_layout.view.*

class CountriesListDataAdapter(private val context : Context,
                               private val countryList : ArrayList<CountriesItem>,
                               private val clickPositionInterface : ClickInterface.ClickPositionInterface) : RecyclerView.Adapter<CountriesListDataAdapter.CountriesListDataAdapterVH>() {
    inner class CountriesListDataAdapterVH(private val itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(country: String, position: Int) {
            itemView.tv_category_name.text = country

            itemView.setOnClickListener {
                clickPositionInterface.clickPostion(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountriesListDataAdapterVH {
        val view= LayoutInflater.from(context).inflate(R.layout.categories_name_list_layout, parent, false)
        return CountriesListDataAdapterVH(view)
    }

    override fun onBindViewHolder(holder: CountriesListDataAdapterVH, position: Int) {
        val country = if (SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang,""].equals("ar")){
            countryList[position].country_name_ar
        }else{
            countryList[position].country_name
        }
        if (country != null) {
            holder.bind(country, position)
        }
    }

    override fun getItemCount(): Int {
        return countryList.size
    }
}