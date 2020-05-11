package com.blondi.firebasemessenger.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.blondi.firebasemessenger.R
import kotlinx.android.synthetic.main.picture_recycler_cell.view.*

class PicturesRecyclerAdapter : RecyclerView.Adapter<PicturesRecyclerAdapter.ViewHolder>() {
    var picturesList = mutableListOf<String>()
    fun insertPictures(pictures : MutableList<String>){
        picturesList=pictures
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.picture_recycler_cell, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return  picturesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(picturesList[position])
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var picture = itemView.findViewById<ImageView>(R.id.pictureHolder)
        fun bind(uri : String){
            //TODO bind from uri
        }
    }
}