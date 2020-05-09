package com.blondi.firebasemessenger.ui.adapters

import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blondi.firebasemessenger.R
import com.blondi.firebasemessenger.models.Inbox
import com.blondi.firebasemessenger.utils.FirebaseMessengerApp
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*


class UserMessagesAdapter() : RecyclerView.Adapter<UserMessagesAdapter.ViewHolder>() {
    lateinit var mOnInboxClickListener: OnInboxClickListener
    private var messageRooms = mutableListOf<Inbox>()
    fun insertMessageRooms(
        listOfMessageRooms: MutableList<Inbox>,
        onInboxClickListener: OnInboxClickListener
    ) {
        messageRooms = listOfMessageRooms
        mOnInboxClickListener = onInboxClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                com.blondi.firebasemessenger.R.layout.chat_room_recycler_cell,
                parent,
                false
            ),
            mOnInboxClickListener
        )
    }

    override fun getItemCount(): Int {
        return messageRooms.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messageRooms[position])
    }


    class ViewHolder(view: View, onInboxClickListener: OnInboxClickListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val userNickname = itemView.findViewById<TextView>(R.id.chatRoomUserNickname)
        val lastMessage = itemView.findViewById<TextView>(R.id.chatRoomMessage)
        val recipientImage = itemView.findViewById<CircleImageView>(R.id.chatRoomUserImage)
        val messageIndicator = itemView.findViewById<ImageView>(R.id.seenIndicator)
        val timeStamp = itemView.findViewById<TextView>(R.id.timeStamp)
        val onInboxClickListener = onInboxClickListener
        fun bind(inbox: Inbox) {
            Log.d("BINDddd", inbox.lastMessage.toString())
            userNickname.text = inbox.recipientNickname
            if (inbox.lastMessage?.shouldBeBold == true) {
                lastMessage.setTextColor(
                    ContextCompat.getColor(
                        FirebaseMessengerApp.getAppContext(),
                        com.blondi.firebasemessenger.R.color.whiteTextColor
                    )
                )
                lastMessage.setTypeface(Typeface.DEFAULT_BOLD)
                lastMessage.text = inbox.lastMessage?.message.toString()
                messageIndicator.setImageResource(R.drawable.circle)
                messageIndicator.visibility = View.VISIBLE
            } else {
                messageIndicator.visibility = View.GONE
                lastMessage.setTypeface(Typeface.DEFAULT)
                lastMessage.setTextColor(
                    ContextCompat.getColor(
                        FirebaseMessengerApp.getAppContext(),
                        R.color.disabledButtonGrey
                    )
                )
                lastMessage.text = inbox.lastMessage?.message.toString()
                if (inbox.lastMessage?.seenStatus == true) {
                    messageIndicator.setImageResource(R.drawable.seen)
                    messageIndicator.visibility = View.VISIBLE
                }
            }

            val formaterJustTime = SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            )
            val formaterDateAndTime = SimpleDateFormat(
                "dd.MM.yyyy. -- HH:mm",
                Locale.getDefault()
            )
            val formater1 = SimpleDateFormat(
                "dd.MM.yyyy",
                Locale.getDefault()
            )
            if (formater1.format(inbox.lastChangeTimeStamp) == formater1.format(System.currentTimeMillis())) {
                timeStamp.text = formaterJustTime.format(inbox.lastChangeTimeStamp)
            } else {
                timeStamp.text = formaterDateAndTime.format(inbox.lastChangeTimeStamp)
            }


            Picasso.get()
                .load(inbox.inboxImage)
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerInside()
                .into(recipientImage)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            onInboxClickListener.onInboxClick(adapterPosition)
        }
    }

    interface OnInboxClickListener {
        fun onInboxClick(position: Int)
    }
}