package com.blondi.firebasemessenger.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.blondi.firebasemessenger.R
import com.blondi.firebasemessenger.common.MESSAGE_TYPE_GIF_MESSAGE
import com.blondi.firebasemessenger.models.Message
import com.blondi.firebasemessenger.utils.FirebaseMessengerApp
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.inbox_message_recycler_cell.view.*
import java.util.zip.Inflater

class inboxMessagesAdapter :RecyclerView.Adapter<inboxMessagesAdapter.ViewHolder>() {
    var messagesList = mutableListOf<Message>()
    fun getSize():Int{return messagesList.size}
    fun insertMessages(messages : MutableList<Message>){
            messagesList=messages
        if(messagesList.last().seenStatus==true){
            for(i in 0..messagesList.size-2){
                messagesList[i].seenStatus=false
            }
        }
        }
    fun addNewMessage(message: Message){messagesList.add(messagesList.size,message) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
     return ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.inbox_message_recycler_cell, parent, false))
    }

    override fun getItemCount(): Int {
        Log.d("RecyclerWithMessages",messagesList.size.toString())
        return messagesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messagesList[position])
    }
    class  ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val userID =FirebaseAuth.getInstance().currentUser?.uid.toString()

        fun bind(message: Message) {
            Log.d("BindMessages","CALLED")
            val myMessagesHolder = itemView.findViewById<LinearLayout>(R.id.senderMessages)
            val myMessageText = itemView.findViewById<TextView>(R.id.messageText)
            val recipientGifHolder = itemView.findViewById<ImageView>(R.id.recipientGifHolder)
            val recipientMessagesHolder = itemView.findViewById<LinearLayout>(R.id.recipientMessages)
            val recipientMessageText = itemView.findViewById<TextView>(R.id.messageTextRecipient)
            val inboxSeenIndicator = itemView.findViewById<ImageView>(R.id.inboxSeenIndicator)
            val myGifHolder =itemView.findViewById<ImageView>(R.id.myGifHolder)
            if(message.senderUID==userID){
                //MOJE PORUKE
                myMessagesHolder.visibility=View.VISIBLE
                recipientMessagesHolder.visibility=View.INVISIBLE
                if(message.messageType==MESSAGE_TYPE_GIF_MESSAGE){
                    myMessageText.visibility=View.GONE
                    myGifHolder.visibility=View.VISIBLE
                    Glide.with(FirebaseMessengerApp.getAppContext())
                        .load(message.message.toString())
                        .into(myGifHolder)
                }else{
                    myGifHolder.visibility=View.GONE
                    myMessageText.visibility=View.VISIBLE
                    myMessageText.text=message.message
                }
                if(message.seenStatus==true){
                    inboxSeenIndicator.visibility=View.VISIBLE
                }else{
                    inboxSeenIndicator.visibility=View.GONE
                }
            }
            else {
                //NJEGOGVE PORUKE
                recipientMessagesHolder.visibility=View.VISIBLE
                myMessagesHolder.visibility=View.INVISIBLE
                if(message.messageType== MESSAGE_TYPE_GIF_MESSAGE){
                    recipientMessageText.visibility=View.GONE
                    recipientGifHolder.visibility=View.VISIBLE
                    Glide.with(FirebaseMessengerApp.getAppContext())
                        .load(message.message.toString())
                        .into(recipientGifHolder)
                }else {
                    recipientGifHolder.visibility=View.GONE
                    recipientMessageText.visibility=View.VISIBLE
                    recipientMessageText.text=message.message
                }
            }
        }
    }
}