package com.seen.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.seen.user.R
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Gallery
import kotlinx.android.synthetic.main.item_small_gallery.view.*

class GalleryListAdapter(private val context: Context, private val data: ArrayList<Gallery>, private val clickInstance: ClickInterface.ClickPosInterface): RecyclerView.Adapter<GalleryListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

       /* return if(viewType==0){
            val view= LayoutInflater.from(context).inflate(R.layout.item_large_gallery, parent, false)
            MyViewHolder(view)
        } else if(viewType==2){
            val view= LayoutInflater.from(context).inflate(R.layout.item_medium_gallery, parent, false)
            MyViewHolder(view)
        } else{
            val view= LayoutInflater.from(context).inflate(R.layout.item_small_gallery, parent, false)
            MyViewHolder(view)
        }*/
        val view= LayoutInflater.from(context).inflate(R.layout.item_small_gallery, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
      /*  when (holder.itemViewType) {
            0 -> {
                holder.itemView.bigView.setImageResource(data[position].icon)

            }
            2 -> {
                holder.itemView.mediumView.setImageResource(data[position].icon)

            }
            else -> {
                holder.itemView.smallView.setImageResource(data[position].icon)

            }
        }*/
        if(data[position].files_path != data[position].thumbnail){
            holder.itemView.imgPlay.visibility=View.VISIBLE
        }
        else{
            holder.itemView.imgPlay.visibility=View.GONE
        }
        Glide.with(context).load(data[position].thumbnail).centerCrop().placeholder(R.drawable.default_icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.itemView.smallView)
        holder.itemView.setOnClickListener {
            clickInstance.clickPostion(position, "")
        }
    }
   /* override fun getItemViewType(position: Int): Int {
        var viewType = 1//Default is 1
        if (position == 0) viewType = 0 //if zero, it will be a header view

        else if (position == 4 || position==accessValue){
            accessValue=position+6
            viewType = 2
        }

        return viewType
    }*/

    override fun getItemCount(): Int {
        return data.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    /* class MyViewHolder2(itemView: View): RecyclerView.ViewHolder(itemView)*/
}