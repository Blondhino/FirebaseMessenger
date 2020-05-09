package com.blondi.firebasemessenger.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blondi.firebasemessenger.R
import com.blondi.firebasemessenger.models.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class usersRecyclerViewAdapter: RecyclerView.Adapter<usersRecyclerViewAdapter.ViewHolder>() {
    var userList = mutableListOf<User>()
    lateinit var monUserClickListener: ViewHolder.OnUserClickListener
    fun insertUsers(users:MutableList<User>,onUserClickListener:ViewHolder.OnUserClickListener){userList=users
        monUserClickListener=onUserClickListener
        for (i in 0 until userList.size){
            Log.d("usersDetails",userList[i].uid+" "+userList[i].imageURI+" "+userList[i].nickName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_user_cell, parent, false),
            monUserClickListener
        )
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userList[position])
    }


    class ViewHolder(view: View, onUserclickListener: OnUserClickListener): RecyclerView.ViewHolder(view),View.OnClickListener {
        var view = view
        val nicknameTV = view.findViewById<TextView>(R.id.userNickname)
        val profileImage = view.findViewById<CircleImageView>(R.id.userImage)
        val onUserClick = onUserclickListener
        fun bind(user : User){
            view.setOnClickListener(this)
            nicknameTV.text = user.nickName
            Log.d("UserImage:",user.imageURI.toString())
            Picasso.get()
                .load(user.imageURI)
                .placeholder(R.drawable.placeholder)
                .fit()
               .centerInside()
               .into(profileImage)
        }
        override fun onClick(view: View?) {
            onUserClick.onUserClick(adapterPosition)
        }

        interface OnUserClickListener{
            fun onUserClick(position : Int)
        }
    }
}