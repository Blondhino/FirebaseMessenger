package com.blondi.firebasemessenger.presentation

import android.content.Context
import android.util.Log

import com.blondi.firebasemessenger.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import com.blondi.firebasemessenger.Network.GiphyAPI
import com.blondi.firebasemessenger.common.MESSAGE_TYPE_GIF_MESSAGE
import com.blondi.firebasemessenger.common.OPTIONS_BUTTON_GIF
import com.blondi.firebasemessenger.common.OPTIONS_BUTTON_PICTURE
import com.blondi.firebasemessenger.models.networkModels.ListOfGifs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.provider.MediaStore
import com.blondi.firebasemessenger.utils.FirebaseMessengerApp
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import com.blondi.firebasemessenger.ui.Activities.MainActivity
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.facebook.FacebookSdk.getApplicationContext
import java.util.jar.Manifest
import android.provider.MediaStore.MediaColumns
import android.app.Activity
import android.database.Cursor
import android.net.Uri
import com.blondi.firebasemessenger.ui.Activities.InboxActivity


class InboxPresenter(context :Context, view: View) {
    val view =view
    var messagesList = mutableListOf<Message>()
    val database = FirebaseDatabase.getInstance().reference
    val firebase = FirebaseAuth.getInstance()
    val userId = firebase.currentUser?.uid.toString()
    var userNickname =""
    var handler=android.os.Handler()
    var isFileChoserVisible = false
    var isKeyboardUp = false
    var userImageUri :String =""
    var currentVisibleOptions = OPTIONS_BUTTON_PICTURE
    lateinit var retrofit : Retrofit
    lateinit var  call :Call<ListOfGifs>
    lateinit var colapsingView: android.view.View
    lateinit var  parrentLinearLayout: LinearLayout
    var gifUrlList = mutableListOf<String>()
    var recipientUid = String()
        set(value) {
            field = value
        }
    var recipientNickname : String = ""
        set(value) {
            field = value
        }
    var recipientImageUri :String =""
        set(value) {
            field = value
        }
    var inboxId: String =""
        set(value) {
            field = value
        }
    var inboxExist = false
        set(value) {
            field = value
        }

    fun checkIfInboxExist(recipientId:String, recipientNickname:String, recipientImageUri:String){
        Log.d("checkIfInboxExist","recipient Id : $recipientId recipientNick : $recipientNickname recipientImageUri: $recipientImageUri userNickName: $userNickname, userImageUrl: $userImageUri")
        recipientUid=recipientId
        this.recipientNickname = recipientNickname
        this.recipientImageUri=recipientImageUri
        val inboxId1 = firebase.currentUser?.uid.toString()+recipientId
        val inboxId2 = recipientId+firebase.currentUser?.uid.toString()
        val childRef = database.child("users").child(userId).child("inbox")
        childRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("dataSnapshootCheck",dataSnapshot.toString())
                if(dataSnapshot.hasChild(inboxId1)){
                    Log.d("dataSnapshootCheck","inbox has id1 : $inboxId1")
                    inboxId=inboxId1
                    getMessagesForThisInbox(inboxId1)
                    childRef.removeEventListener(this)
                    inboxExist=true
                }
                if(dataSnapshot.hasChild(inboxId2)){
                    Log.d("dataSnapshootCheck","inbox has id1 : $inboxId2")
                    inboxId=inboxId2
                    getMessagesForThisInbox(inboxId2)
                    childRef.removeEventListener(this)
                    inboxExist=true
                }
                if(!dataSnapshot.hasChild(inboxId1) && !dataSnapshot.hasChild(inboxId2) ) {
                    Log.d("dataSnapshootCheck","inboxId = $inboxId")
                  childRef.removeEventListener(this)
                    inboxExist=false
                    startInitialListener(recipientId)
                }
            }
        })
    }

    fun sendMessage(message : Message, recipientId: String){
        recipientUid=recipientId
        Log.d("SendMessage","recipientID: $recipientId , InboxID: $inboxId , inboxExist : $inboxExist , message : ${message.message.toString()}")
        if(inboxId==""){
            var key = database.child("users").child(userId).child("inbox").child(userId+recipientId).child("messages").push().key
            database.child("users").child(userId).child("inbox").child(userId+recipientId).child("messages").child(key!!).setValue(message)
            database.child("users").child(userId).child("inbox").child(userId+recipientId).child("messages").child(key!!).child("messageId").setValue(key!!)

            var keyRecipient = database.child("users").child(recipientId).child("inbox").child(userId+recipientId).child("messages").push().key
            database.child("users").child(recipientId).child("inbox").child(userId+recipientId).child("messages").child(keyRecipient!!).setValue(message)
            database.child("users").child(recipientId).child("inbox").child(userId+recipientId).child("messages").child(keyRecipient!!).child("messageId").setValue(keyRecipient!!)

            database.child("users").child(userId).child("inbox").child(userId+recipientId).child("lastChangeTimeStamp").setValue(ServerValue.TIMESTAMP)
            database.child("users").child(recipientId).child("inbox").child(userId+recipientId).child("lastChangeTimeStamp").setValue(ServerValue.TIMESTAMP)

            database.child("users").child(userId).child("inbox").child(userId+recipientId).child("lastMessage").setValue(message)
            database.child("users").child(recipientId).child("inbox").child(userId+recipientId).child("lastMessage").setValue(message)
            database.child("users").child(userId).child("inbox").child(userId+recipientId).child("recipientUID").setValue(recipientId)
            database.child("users").child(recipientId).child("inbox").child(userId+recipientId).child("recipientUID").setValue(userId)
            if(inboxExist==false){
                database.child("users").child(recipientId).child("inbox").child(userId+recipientId).child("uid").setValue(userId+recipientId)
                database.child("users").child(userId).child("inbox").child(userId+recipientId).child("uid").setValue(userId+recipientId)
                database.child("users").child(userId).child("inbox").child(userId+recipientId).child("inboxImage").setValue(recipientImageUri)
                database.child("users").child(recipientId).child("inbox").child(userId+recipientId).child("inboxImage").setValue(userImageUri)
                database.child("users").child(userId).child("inbox").child(userId+recipientId).child("recipientNickname").setValue(recipientNickname)
                database.child("users").child(recipientId).child("inbox").child(userId+recipientId).child("recipientNickname").setValue(userNickname)

            }
        }
        else {
            var key = database.child("users").child(userId).child("inbox").child(inboxId).child("messages").push().key
            database.child("users").child(userId).child("inbox").child(inboxId).child("messages").child(key!!).setValue(message)
            database.child("users").child(userId).child("inbox").child(inboxId).child("messages").child(key!!).child("messageId").setValue(key!!)
            var keyRecipient = database.child("users").child(recipientId).child("inbox").child(inboxId).child("messages").push().key
            database.child("users").child(recipientId).child("inbox").child(inboxId).child("messages").child(keyRecipient!!).setValue(message)
            database.child("users").child(recipientId).child("inbox").child(inboxId).child("messages").child(keyRecipient!!).child("messageId").setValue(keyRecipient!!)
            database.child("users").child(userId).child("inbox").child(inboxId).child("lastMessage").setValue(message)
            database.child("users").child(recipientId).child("inbox").child(inboxId).child("lastMessage").setValue(message)
            database.child("users").child(userId).child("inbox").child(inboxId).child("lastChangeTimeStamp").setValue(ServerValue.TIMESTAMP)
            database.child("users").child(recipientId).child("inbox").child(inboxId).child("lastChangeTimeStamp").setValue(ServerValue.TIMESTAMP)
        }
    }

    fun getMessagesForThisInbox(inboxId: String){

        if(userImageUri==""){
            val userImageref =database.child("users").child(userId).child("imageURI")
            userImageref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("userImage",dataSnapshot.toString())
                    userImageUri=dataSnapshot.value.toString()
                    userImageref.removeEventListener(this) } })
        }
        if(userNickname==""){
            val userNicknameRef = database.child("users").child(userId).child("nickName")
            userNicknameRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("userNickname",dataSnapshot.toString())
                    userNickname=dataSnapshot.value.toString()
                    userNicknameRef.removeEventListener(this)
                }
            })
        }

        Log.d("getMessagesFor","inboxId = $inboxId")
        val inboxRef =database.child("users").child(userId).child("inbox").child(inboxId).child("messages")
        val query = inboxRef.orderByKey().limitToLast(100)
        query.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    messagesList =dataSnapshot.children.mapNotNullTo(messagesList){
                        it.getValue<Message>(Message::class.java)
                    }
                    view.fillAdapterWith(messagesList)
                    query.removeEventListener(this)
                    startListeningForNewMessages()
                    seenLastMessage()
                    startListeningForSeen(inboxId)
                }
            }
        })
    }

    fun startListeningForNewMessages(){
        var counter= 0
        val inboxRef =database.child("users").child(userId).child("inbox").child(inboxId).child("messages")
        inboxRef.orderByKey().limitToLast(1).addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                if(counter>0){
                    Log.d("dataSnapshoot_ca",dataSnapshot.toString())
                    val message = dataSnapshot.getValue(Message::class.java)
                    view.insertNewMessage(message!!)
                    counter =0
                }
                counter++
            }
            override fun onChildRemoved(p0: DataSnapshot) {}

        })
    }
    private fun startInitialListener(recipientId: String) {
        Log.d("initialListener","called")
        val inboxId1 = firebase.currentUser?.uid.toString()+recipientId
        val inboxId2 = recipientId+firebase.currentUser?.uid.toString()
        val listener1 = database.child("users").child(userId)
        val listener2= database.child("users").child(userId).child("inbox")
        var counter =0
        if(userImageUri==""){
            val userImageref =database.child("users").child(userId).child("imageURI")
            userImageref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("userImage",dataSnapshot.toString())
                    userImageUri=dataSnapshot.value.toString()
                    userImageref.removeEventListener(this) } })
        }
        if(userNickname==""){
            val userNicknameRef = database.child("users").child(userId).child("nickName")
            userNicknameRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("userNickname",dataSnapshot.toString())
                    userNickname=dataSnapshot.value.toString()
                    userNicknameRef.removeEventListener(this)
                }
            })
        }
        listener1.addChildEventListener(object: ChildEventListener{
           override fun onCancelled(p0: DatabaseError) {}
           override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
           override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
           override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

               Log.d("dataSnapshoot_initial",dataSnapshot.value.toString()+"  has inbox v1: "+dataSnapshot.hasChild(inboxId1).toString()+"  has inbox v2: "+dataSnapshot.hasChild(inboxId2).toString())
            if(dataSnapshot.hasChild(inboxId1)){
                listener1.removeEventListener(this)
                inboxId=inboxId1
                getMessagesForThisInbox(inboxId1)
                Log.d("initialListener","getting messages fo inboxId : $inboxId1")
            }
               if(dataSnapshot.hasChild(inboxId2)){
                   listener1.removeEventListener(this)
                   inboxId=inboxId2
                   getMessagesForThisInbox(inboxId2)
                   Log.d("initialListener","getting messages fo inboxId : $inboxId2")
               }

           }
           override fun onChildRemoved(p0: DataSnapshot) {}
       })
       listener2.addChildEventListener(object :ChildEventListener{
           override fun onCancelled(p0: DatabaseError) {}
           override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
           override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
           override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
               if(counter > 0 ) {
                   counter = 0
                   Log.d("listener2","called, datasnapsooht : \n${dataSnapshot.toString()}")
                   if(dataSnapshot.key==inboxId1 || dataSnapshot.key==inboxId2){
                       getMessagesForThisInbox(dataSnapshot.key.toString())
                   }
               }
               counter ++
           }
           override fun onChildRemoved(p0: DataSnapshot) {}
       })
    }

    fun seenLastMessage() {
        if(messagesList.last().senderUID!=userId){
            database.child("users").child(messagesList.last().senderUID.toString()).child("inbox")
                .child(inboxId).child("lastMessage").child("seenStatus").setValue(true)

            database.child("users").child(userId).child("inbox")
                .child(inboxId).child("lastMessage").child("seenStatus").setValue(true)
        }
    }

    fun startListeningForSeen(inboxId: String){
        val ref = database.child("users").child(userId).child("inbox").child(inboxId).child("lastMessage")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                Log.d("SeenReport:",dataSnapshot.toString())
                if(dataSnapshot.key=="seenStatus"&&dataSnapshot.value==true){

                    handleSeen(messagesList.last().messageId)
                }
            }
            override fun onChildRemoved(p0: DataSnapshot) {}
            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {}
        })
    }

    private fun handleSeen(messageId: String?) {
        for(i in 0 until messagesList.size){
            if(messagesList[i].messageId!=messageId){
                messagesList[i].seenStatus=false}
            else{messagesList[i].seenStatus=true }
        }
        view.fillAdapterWith(messagesList)
        Log.d("handleSeenReport",messagesList.toString())
    }

    fun fileButtonClicked(colapsingView: android.view.View, parrentLinearLayout: LinearLayout) {
        this.colapsingView=colapsingView
        this.parrentLinearLayout=parrentLinearLayout
        if(isFileChoserVisible){
                colapseHandler(true)
        }else{
            if(isKeyboardUp){
                view.colapseKeyboard()
                handler.postDelayed({colapseHandler(false)},100)
            }else {
                colapseHandler(false)
            }

        }
    }



     fun colapseHandler(shouldBeCollapsed: Boolean) {
        if(shouldBeCollapsed){
            (colapsingView.layoutParams as LinearLayout.LayoutParams).weight=100F
            parrentLinearLayout.requestLayout()
            isFileChoserVisible=false

        }
        else {
            (colapsingView.layoutParams as LinearLayout.LayoutParams).weight=52F
            parrentLinearLayout.requestLayout()
            isFileChoserVisible=true
        }
    }

    fun setKeyboardListener(contentView: android.view.View) {
        contentView.viewTreeObserver.addOnGlobalLayoutListener {
            val rec = Rect()
            contentView.getWindowVisibleDisplayFrame(rec)
            val screenHeight = contentView.rootView.height
            val keypadHeight = screenHeight - rec.bottom
            if (keypadHeight > screenHeight * 0.15) {
                Log.d("KeyboardChecker","Keyboard UP")
                isKeyboardUp=true
                if(isFileChoserVisible){
                    if(view.isInputMesageEditTextFocused())
                    {
                        colapseHandler(true)
                    }

                    }

            } else {
                isKeyboardUp=false
                Log.d("KeyboardChecker","No Keyboard")
            }
        }
    }

    fun getGifs(searchTerm: String) {
        var urls = mutableListOf<String>()
        val giphyAPI = retrofit.create(GiphyAPI::class.java)
        call = giphyAPI.getGIFs("0mTB76DriafisEc1lcRvjHZGSpHtvRgd",searchTerm,"50")
        call.enqueue(object : Callback<ListOfGifs> {
            override fun onFailure(call: Call<ListOfGifs>, t: Throwable) {
                Log.d("APIcall","onFailure: ${t.message.toString()}" )
            }

            override fun onResponse(call: Call<ListOfGifs>, response: Response<ListOfGifs>) {
                Log.d("APIcall","onResponse: ${response.body().toString()}")
                var gif = response.body()!!
                for(i in 0 until gif.data!!.size){
                    Log.d("APIcallGIF","response: ${gif.data!![i].images!!.fixed_width_small!!.url.toString()}")
                    urls.add(gif.data!![i].images!!.fixed_width_small!!.url.toString())
                }
               view.fillGifAdapterWith(urls)
                gifUrlList=urls
            }
        })
    }

    fun setUpRetrofit() {
        retrofit = Retrofit.Builder()
            .baseUrl("https://api.giphy.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun onGifClicked(position: Int) {
       sendMessage(Message(userId,gifUrlList[position], MESSAGE_TYPE_GIF_MESSAGE),recipientUid)
        Log.d("OnGifClicked","userId: $userId  recipientId: $recipientUid inboxId: $inboxId message : ${gifUrlList[position]}")
    }

    fun gisRecyclerScrolling(dy: Int) {

       if(dy > 0){
            Log.d("OnGifScroll","UP")
           view.animate(false)
        }
        if(dy < 0){Log.d("OnGifScroll","DOWN")
            view.animate(false)
        }
    }

    fun gifRecyclerStateChanged(state: Int) {
        if(state == 0){Log.d("OnGifScroll","STOPPED")
            view.animate(true)
        }
    }

    fun optionsButtonClicked(optionsButton: String) {
        if(optionsButton== OPTIONS_BUTTON_PICTURE && currentVisibleOptions== OPTIONS_BUTTON_GIF){
            view.picturesOptionsSetVisibility(true)
            view.gifOPtionsSetVisibility(false)
            currentVisibleOptions= OPTIONS_BUTTON_PICTURE
            }
        if(optionsButton== OPTIONS_BUTTON_GIF && currentVisibleOptions== OPTIONS_BUTTON_PICTURE)
        {
            view.picturesOptionsSetVisibility(false)
            view.gifOPtionsSetVisibility(true)
            currentVisibleOptions= OPTIONS_BUTTON_GIF
        }

        }




    interface View{
        fun fillAdapterWith(messages :MutableList<Message>)
        fun fillGifAdapterWith(gifUrls:MutableList<String>)
        fun insertNewMessage(message: Message)
        fun colapseOptionsMenu()
        fun colapseKeyboard()
        fun isInputMesageEditTextFocused(): Boolean
        fun animate(show: Boolean)
        fun gifOPtionsSetVisibility(visibile : Boolean)
        fun picturesOptionsSetVisibility(visibile : Boolean)
    }
}