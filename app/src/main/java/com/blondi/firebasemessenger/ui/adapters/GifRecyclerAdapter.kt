package com.blondi.firebasemessenger.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.blondi.firebasemessenger.R
import com.blondi.firebasemessenger.utils.FirebaseMessengerApp
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.gif_recycler_cell.view.*

class GifRecyclerAdapter: RecyclerView.Adapter<GifRecyclerAdapter.ViewHolder>() {
    var listOfUrls = mutableListOf<String>()
    lateinit var onGifClickListener: OnGifClickListener
    fun insertGifs(urls : MutableList<String>, onGifClickListener: OnGifClickListener){
        this.onGifClickListener=onGifClickListener
        listOfUrls=urls}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.gif_recycler_cell,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return listOfUrls.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfUrls[position],onGifClickListener)
    }


    class ViewHolder(view : View) : RecyclerView.ViewHolder(view),View.OnClickListener {
        var gifPreview = itemView.findViewById<ImageView>(R.id.gifPreviewImageView)
        var onGifClickListener:OnGifClickListener?=null
        fun bind(url: String, onGifClickListener: OnGifClickListener){
            this.onGifClickListener=onGifClickListener
        itemView.setOnClickListener(this)
        Glide.with(FirebaseMessengerApp.getAppContext())
            .load(url)
            .placeholder(R.drawable.gif_placeholder)
            .into(gifPreview)
        }

        override fun onClick(view: View) {
           onGifClickListener?.onGifClick(adapterPosition)
        }
    }

    interface OnGifClickListener {
        fun onGifClick(position: Int)
    }
}