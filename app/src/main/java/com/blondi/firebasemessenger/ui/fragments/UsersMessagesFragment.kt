package com.blondi.firebasemessenger.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blondi.firebasemessenger.R
import com.blondi.firebasemessenger.models.Inbox
import com.blondi.firebasemessenger.ui.fragments.fragment_presentation.UserMessagesFragmentPresenter
import com.blondi.firebasemessenger.ui.Activities.MessagesActivity
import com.blondi.firebasemessenger.ui.adapters.UserMessagesAdapter
import com.blondi.firebasemessenger.utils.FirebaseMessengerApp
import kotlinx.android.synthetic.main.big_plus_button.*
import kotlinx.android.synthetic.main.fragment_users_messages.*

class UsersMessagesFragment :Fragment(), UserMessagesFragmentPresenter.View, UserMessagesAdapter.OnInboxClickListener {
    val presenter = UserMessagesFragmentPresenter(this)
    var isInitialRetrivingDone = false
    lateinit var recyclerAdapter :UserMessagesAdapter
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_users_messages,container,false)}  //TODO make layout
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUI()
        retriveMessages()
        setupRecycler()
    }

     private fun retriveMessages() {
         if(!isInitialRetrivingDone) {
             presenter.retriveAndShowMessages()
             isInitialRetrivingDone=true
         }else {
             presenter.startListeningForExistingBoxesForChanges()
         }
    }

    private fun setupRecycler() {
        usersMessagesRecycler.apply {
            layoutManager=LinearLayoutManager(FirebaseMessengerApp.getAppContext())
            recyclerAdapter= UserMessagesAdapter()
            adapter=recyclerAdapter
        }
    }

    private fun initUI() {
        bigPlusButton.setOnClickListener { presenter.bigPlusButtonClicked() }
    }


    override fun isBigPlusButtonVisible(isVisible: Boolean) {
        if(isVisible){bigPlusButtonContainer.visibility=View.VISIBLE}
        else{bigPlusButtonContainer.visibility=View.INVISIBLE}
    }
    override fun swipeTo(position: Int) {
        (activity as MessagesActivity).swipe(position)
    }
    override fun fillAdapterWithChatRooms(chatRooms: MutableList<Inbox>) {
     recyclerAdapter.insertMessageRooms(chatRooms, this)
        recyclerAdapter.notifyDataSetChanged()
    }

    override fun onInboxClick(position: Int) {
        presenter.clickedOnInbox(position)
    }


}