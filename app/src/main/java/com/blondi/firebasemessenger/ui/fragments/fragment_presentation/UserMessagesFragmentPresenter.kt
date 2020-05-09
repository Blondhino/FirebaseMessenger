package com.blondi.firebasemessenger.ui.fragments.fragment_presentation

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.blondi.firebasemessenger.common.*
import com.blondi.firebasemessenger.models.Inbox
import com.blondi.firebasemessenger.models.Message
import com.blondi.firebasemessenger.ui.Activities.InboxActivity
import com.blondi.firebasemessenger.utils.FirebaseMessengerApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import kotlin.math.log

class UserMessagesFragmentPresenter(view : View) {
    val database = FirebaseDatabase.getInstance().getReference()
    val firebase = FirebaseAuth.getInstance()
    val userId= firebase.currentUser?.uid.toString()
    val view = view
    var listOfChatRooms = mutableListOf<Inbox>()

    fun retriveAndShowMessages(){
       checkIfUserHasMessages()
       Log.d("TokenFCM: ", FirebaseInstanceId.getInstance().token.toString())
    }

    fun bigPlusButtonClicked(){
        view.swipeTo(1)
    }
    fun checkIfUserHasMessages(){
        val childRef = database.child("users").child(userId)
        childRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot){
                if(!dataSnapshot.hasChild("inbox")){
                    view.isBigPlusButtonVisible(true)
                    startListeningForNewBoxes()
                }else{
                    view.isBigPlusButtonVisible(false)
                    getAllChatRooms()
                }
            }

        })
    }
    private fun getAllChatRooms() {

    val chatRoomsRef = database.child("users").child(userId).child("inbox")
    val query = chatRoomsRef.orderByKey().limitToLast(20)
        query.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("CHAT_ROOMS_",dataSnapshot.toString())
                    if(dataSnapshot.exists()){
                        listOfChatRooms =dataSnapshot.children.mapNotNullTo(listOfChatRooms){
                            it.getValue<Inbox>(Inbox::class.java)
                        }
                        view.fillAdapterWithChatRooms(listOfChatRooms)
                        query.removeEventListener(this)
                        startListeningForExistingBoxesForChanges()
                        startListeningForNewBoxes()
                    }
            }
        })
    }

     fun startListeningForNewBoxes() {
        val chatRoomsRef = database.child("users").child(userId).child("inbox")
        var counter =0
        chatRoomsRef.addChildEventListener(object :ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                Log.d("NewBoxesCalled", dataSnapshot.key.toString()+" counter: "+counter.toString()+" list size :"+listOfChatRooms.size)
               /* if(counter >listOfChatRooms.size-1){
                    Log.d("NewBoxes", dataSnapshot.key.toString())
                    addNewInboxToList(dataSnapshot.key.toString())
                    view.isBigPlusButtonVisible(false)
                    counter=0
                }
                counter++ */
                Log.d("NewBoxes", dataSnapshot.key.toString())
                addNewInboxToList(dataSnapshot.key.toString())
                view.isBigPlusButtonVisible(false)

            }
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }

    private fun addNewInboxToList(boxUid: String) {
        val inboxRef = database.child("users").child(userId).child("inbox").child(boxUid)
        inboxRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val inbox=dataSnapshot.getValue(Inbox::class.java)
                Log.d("NewBoxes",dataSnapshot.toString())
               if( !checkIfInboxExist(inbox?.uid.toString())){
                   listOfChatRooms.add(inbox!!)
               }
                view.fillAdapterWithChatRooms(listOfChatRooms)
                inboxRef.removeEventListener(this)
                startListeningForExistingBoxesForChanges()
            }

        })
    }

    private fun checkIfInboxExist(inboxID:String):Boolean{
        Log.d("CheckingInbox","called")
        for(i in 0..listOfChatRooms.size-1){
            if(listOfChatRooms[i].uid==inboxID){
                Log.d("CheckingInbox","inbox with id: $inboxID already exist")
                return true
            }
            if(i == listOfChatRooms.size-1){
                Log.d("CheckingInbox","inbox with id: $inboxID not exist")
                return false
            }
        }
        return false
    }

    fun startListeningForExistingBoxesForChanges() {
        val chatRoomsRef = database.child("users").child(userId).child("inbox")
        for(i in 0 until listOfChatRooms.size){
            chatRoomsRef.child(listOfChatRooms[i].uid.toString())
                .addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.d("ExistingChng",dataSnapshot.toString())
                      var inbox = dataSnapshot.getValue(Inbox::class.java)
                       updateListOfChatRooms(inbox!!)
                    }

                })
        }
    }


    private fun updateListOfChatRooms(inbox:Inbox) {  //promjena se desila u inboxu
    for(i in 0 until listOfChatRooms.size){
        if(listOfChatRooms[i].uid==inbox.uid) {
            if(inbox.lastMessage?.senderUID==userId){  //provjera jel nova poruka od mene
               listOfChatRooms[i].lastMessage?.message="You: "+inbox.lastMessage?.message.toString() //dodaj "you" ispred teksta poruke
               listOfChatRooms[i].lastChangeTimeStamp=inbox.lastChangeTimeStamp // napisi datum
               listOfChatRooms[i].lastMessage?.shouldBeBold=false //skloni bold
               if(inbox.lastMessage?.seenStatus==true){ //provjeri je li seenana
                  listOfChatRooms[i].lastMessage?.seenStatus=true //postavi status true ako je seenana
               } else{listOfChatRooms[i].lastMessage?.seenStatus=false} //u protivnom status je false
            } else { //poruka je od njega
                listOfChatRooms[i].lastMessage?.message=inbox.lastMessage?.message.toString() //ispisi poruku
                listOfChatRooms[i].lastChangeTimeStamp=inbox.lastChangeTimeStamp // napisi datum
                if(inbox.lastMessage?.seenStatus==true){ //ako sam pogledo poruku
                    listOfChatRooms[i].lastMessage?.shouldBeBold=false //skloni bold
                    listOfChatRooms[i].lastMessage?.seenStatus=false //stavi false, jer na njegovim porukama ne pratim svoj seen
                }else{ //ako nisam pogledo poruku
                    listOfChatRooms[i].lastMessage?.shouldBeBold=true //postavi bold
                    listOfChatRooms[i].lastMessage?.seenStatus=false //stavi false, jer na njegovim porukama ne pratim svoj seen
                }
            }
            listOfChatRooms[i].inboxImage=inbox.inboxImage
            listOfChatRooms[i].recipientNickname=inbox.recipientNickname
        }
    }
        Log.d("inboxes","inbox : ${listOfChatRooms[0].lastMessage}")
        view.fillAdapterWithChatRooms(listOfChatRooms)
    }

    fun clickedOnInbox(position: Int) {
        val bundle= Bundle()
        bundle.putString(SENDER_UID,firebase.currentUser?.uid.toString())
        bundle.putString(RECIPIENT_UID,listOfChatRooms[position].recipientUID)
        bundle.putString(RECIPIENT_NICKNAME,listOfChatRooms[position].recipientNickname)
        bundle.putString(RECIPIENT_IMAGE_URL,listOfChatRooms[position].inboxImage)
        bundle.putString(INBOX_UID,listOfChatRooms[position].uid)
        val intent = Intent(FirebaseMessengerApp.getAppContext(), InboxActivity::class.java)
        intent.putExtras(bundle)
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
        startActivity(FirebaseMessengerApp.getAppContext(),intent,null)
    }

    interface  View {
        fun isBigPlusButtonVisible(isVisible : Boolean)
        fun swipeTo(position : Int)
        fun fillAdapterWithChatRooms(chatRooms : MutableList<Inbox>){}
    }
}