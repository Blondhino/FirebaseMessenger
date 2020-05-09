package com.blondi.firebasemessenger.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blondi.firebasemessenger.Network.GiphyAPI
import com.blondi.firebasemessenger.models.networkModels.Gif
import com.blondi.firebasemessenger.models.networkModels.ListOfGifs
import com.blondi.firebasemessenger.ui.adapters.GifRecyclerAdapter
import com.blondi.firebasemessenger.ui.adapters.inboxMessagesAdapter
import com.blondi.firebasemessenger.utils.FirebaseMessengerApp
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_notifications.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit





class NotificationsFragment :Fragment() {
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.blondi.firebasemessenger.R.layout.fragment_notifications,container,false)}  //TODO make layout
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


    }


}


