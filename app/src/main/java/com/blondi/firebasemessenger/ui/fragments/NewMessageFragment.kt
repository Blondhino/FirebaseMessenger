package com.blondi.firebasemessenger.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blondi.firebasemessenger.R
import com.blondi.firebasemessenger.common.RECIPIENT_IMAGE_URL
import com.blondi.firebasemessenger.common.RECIPIENT_NICKNAME
import com.blondi.firebasemessenger.common.RECIPIENT_UID
import com.blondi.firebasemessenger.common.SENDER_UID
import com.blondi.firebasemessenger.ui.adapters.usersRecyclerViewAdapter
import com.blondi.firebasemessenger.ui.fragments.fragment_presentation.NewMessageFragmentPresenter
import com.blondi.firebasemessenger.models.User
import com.blondi.firebasemessenger.ui.Activities.InboxActivity
import com.blondi.firebasemessenger.utils.FirebaseMessengerApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_new_message.*
import kotlinx.android.synthetic.main.search_bar.*

class NewMessageFragment :Fragment(),NewMessageFragmentPresenter.View,usersRecyclerViewAdapter.ViewHolder.OnUserClickListener {
    val presenter = NewMessageFragmentPresenter(this)
    private val firebase = FirebaseAuth.getInstance()
    var userList = mutableListOf<User>()
    lateinit var recyclerAdapter : usersRecyclerViewAdapter
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_message,container,false)}  //TODO make layout
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initUI()
    }
    private fun initUI() {
        setupRecycler()
        setupListeners()
    }

    private fun setupListeners() {
        search_barEditText?.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(insertedText: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(insertedText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(insertedText.toString().length<=2){presenter.sendEmptyResult()} // does not consume firebase query resources before text.length is less than 3 letters
                else{presenter.searchUsersBy(insertedText.toString())}
            }
        })
    }


    private fun setupRecycler() {
       usersRecycler.apply {
           layoutManager = LinearLayoutManager(FirebaseMessengerApp.getAppContext())
           recyclerAdapter = usersRecyclerViewAdapter()
           adapter = recyclerAdapter
       }
    }

    override fun insertSearchResultToRecycler(users: MutableList<User>) {
        userList=users
        Log.d("QUERY_firebase","insertSrchRes user size: "+ users.size.toString())
        for(i in 0 until users.size){
            Log.d("QUERY_firebase","for loop insertFunction position [$i] nickname :"+ users[i].nickName.toString())
        }
        recyclerAdapter.insertUsers(users,this)
        recyclerAdapter.notifyDataSetChanged()
    }
    override fun onUserClick(position: Int) {
        val bundle=Bundle()
        bundle.putString(SENDER_UID,firebase.currentUser?.uid.toString())
        bundle.putString(RECIPIENT_UID,userList[position].uid.toString())
        bundle.putString(RECIPIENT_NICKNAME,userList[position].nickName)
        bundle.putString(RECIPIENT_IMAGE_URL,userList[position].imageURI)

        val intent =Intent(FirebaseMessengerApp.getAppContext(),InboxActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)

    }
}