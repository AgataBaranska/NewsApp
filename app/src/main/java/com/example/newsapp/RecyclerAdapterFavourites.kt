package com.example.newsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.models.Item

class RecyclerAdapterFavourites(private val itemsList: List<Item>) :
    RecyclerView.Adapter<RecyclerAdapterFavourites.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return RecyclerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = itemsList[position]
        holder.title.text = currentItem.title
        if (currentItem.image == null) {
            holder.img.setImageResource(R.drawable.image_placeholder)
        } else {
            Glide.with(holder.itemView.context).load(currentItem.image).into(holder.img)
        }
        holder.description.text = currentItem.description
        holder.layoutItem.setBackgroundResource(R.drawable.bg_unread_item)
    }


    override fun getItemCount(): Int {
        return itemsList.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_title)
        val img: ImageView = itemView.findViewById(R.id.item_image)
        val description: TextView = itemView.findViewById((R.id.item_description))
        val layoutItem: LinearLayout = itemView.findViewById((R.id.layout_item))
    }


}
