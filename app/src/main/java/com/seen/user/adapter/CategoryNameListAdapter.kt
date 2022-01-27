package com.seen.user.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Categories
import kotlinx.android.synthetic.main.categories_name_list_layout.view.*

class CategoryNameListAdapter(private val context: Context,
private val categoriesList: ArrayList<Categories>,
private val clickPositionInterface : ClickInterface.ClickPositionInterface) : RecyclerView.Adapter<CategoryNameListAdapter.CategoryNameListAdapterVH>() {
    inner class CategoryNameListAdapterVH(private val itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: String, position: Int) {
            itemView.tv_category_name.text = category

            itemView.setOnClickListener {
                clickPositionInterface.clickPostion(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryNameListAdapterVH {
        val view= LayoutInflater.from(context).inflate(R.layout.categories_name_list_layout, parent, false)
        return CategoryNameListAdapterVH(view)
    }

    override fun onBindViewHolder(holder: CategoryNameListAdapterVH, position: Int) {
        val category = categoriesList[position].name
        holder.bind(category, position)
    }

    override fun getItemCount(): Int {
       return categoriesList.size
        Log.e("categoriesListsize", categoriesList.size.toString())
    }
}