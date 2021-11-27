package com.dev.ecommerceuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecommerceuser.R

class SimilarItemsAdapter (private val context: Context) : RecyclerView.Adapter<SimilarItemsAdapter.SimilarItemsAdapterVH>() {
    inner class SimilarItemsAdapterVH(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarItemsAdapterVH {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_similar_items, parent, false)
        return SimilarItemsAdapterVH(view)
    }

    override fun onBindViewHolder(holder: SimilarItemsAdapterVH, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }
}