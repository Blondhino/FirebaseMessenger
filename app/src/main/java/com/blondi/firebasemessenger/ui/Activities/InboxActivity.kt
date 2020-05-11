package com.blondi.firebasemessenger.ui.Activities

import android.animation.ValueAnimator
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Scroller
import androidx.recyclerview.widget.LinearLayoutManager

import com.blondi.firebasemessenger.common.*
import com.blondi.firebasemessenger.models.Message
import com.blondi.firebasemessenger.presentation.InboxPresenter
import com.blondi.firebasemessenger.ui.adapters.inboxMessagesAdapter
import com.blondi.firebasemessenger.utils.FirebaseMessengerApp
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_inbox.*
import kotlinx.android.synthetic.main.inbox_foother.*
import kotlinx.android.synthetic.main.inbox_header.*
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blondi.firebasemessenger.ui.adapters.GifRecyclerAdapter
import kotlinx.android.synthetic.main.options_gifs.*
import kotlinx.android.synthetic.main.options_inbox.*


class InboxActivity : AppCompatActivity(), InboxPresenter.View, GifRecyclerAdapter.OnGifClickListener {
    lateinit var senderID: String
    var iscolapsed = false
    lateinit var recipientID: String
    lateinit var recipientImgUrl: String
    lateinit var recipientNickname: String
    lateinit var bundle: Bundle
    lateinit var recyclerAdapter: inboxMessagesAdapter
    lateinit var gifAdapter: GifRecyclerAdapter
    var automaticSeenState = AUTOMATIC_SEEN_DISABLED
    val layoutManagerVar = LinearLayoutManager(FirebaseMessengerApp.getAppContext())
    private val firebase = FirebaseAuth.getInstance()
    val presenter = InboxPresenter(this, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.blondi.firebasemessenger.R.layout.activity_inbox)
        bundle = intent.extras!!
        catchRecipientData()
        presenter.setUpRetrofit()
        initUI()
    }

    private fun checkInbox() {
        Log.d("inboxID_check", " recipient id : $recipientID recipientNick : $recipientNickname recipitentImageUrl: $recipientImgUrl")
        presenter.checkIfInboxExist(recipientID,recipientNickname,recipientImgUrl)
    }

    private fun initUI() {
        setUpListeners()
        setUpEditText()
        setUpToolbarValues()
        presenter.loadPicturesFromGallery()
        setupRecyclers()
    }

    private fun setUpListeners() {
        var timer : CountDownTimer? = null
        sendMessageButton.setOnClickListener { sendMsg() }
        backArrow.setOnClickListener { onBackPressed() }
        fileButton.setOnClickListener {
            presenter.fileButtonClicked(inboxMessagesLayout, inboxHolderLayout)
        }
        gifButton.setOnClickListener{presenter.optionsButtonClicked(OPTIONS_BUTTON_GIF)}
        pictureButton.setOnClickListener{presenter.optionsButtonClicked(OPTIONS_BUTTON_PICTURE)}
        messageInput.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                Log.d(
                    "FocusListener",
                    "hasFocus = $hasFocus, is file choser visible :${presenter.isFileChoserVisible.toString()}"
                )
                if (hasFocus && presenter.isFileChoserVisible) {
                    presenter.colapseHandler(true)
                    presenter.isFileChoserVisible = false
                }
            }
        searchText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                timer?.cancel()
                timer = object :CountDownTimer(1000,1500){
                    override fun onFinish() {
                    presenter.getGifs(text.toString())
                        Log.d("TimerEditText","Called")
                    }
                    override fun onTick(p0: Long) {}
                }.start()

            }

        })
    }

    private fun sendMsg() {
        if (messageInput.text.trim().isNotEmpty()) {
            presenter.sendMessage(
                Message(firebase.uid.toString(),messageInput.text.trim().toString(),
                    MESSAGE_TYPE_TEXT_MESSAGE), recipientID
            )
            messageInput.text.clear()
        }
    }

    private fun setUpToolbarValues() {
        toolbar_nickname.text = recipientNickname
        setImage()

    }

    private fun setupRecyclers() {
        inbox_messages_recycler_view.apply {
            layoutManagerVar.stackFromEnd = true
            layoutManager = layoutManagerVar
            recyclerAdapter = inboxMessagesAdapter()
            adapter = recyclerAdapter
        }
        gifRecycler.apply {
            layoutManager = GridLayoutManager(FirebaseMessengerApp.getAppContext(), 2)
            gifAdapter = GifRecyclerAdapter()
            adapter = gifAdapter

        }.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                presenter.gisRecyclerScrolling(dy)
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                presenter.gifRecyclerStateChanged(newState)
            }
        })

    }

    private fun setImage() {
        Picasso.get()
            .load(recipientImgUrl)
            .placeholder(com.blondi.firebasemessenger.R.drawable.placeholder)
            .into(toolbar_image)
    }

    private fun setUpEditText() {
        messageInput.setScroller(Scroller(FirebaseMessengerApp.getAppContext()))
        messageInput.maxLines = 3
        messageInput.isVerticalScrollBarEnabled = true
        messageInput.movementMethod = ScrollingMovementMethod()
    }

    private fun catchRecipientData() {
        senderID = bundle.getString(SENDER_UID)!!
        recipientID = bundle.getString(RECIPIENT_UID)!!
        recipientNickname = bundle.getString(RECIPIENT_NICKNAME)!!
        recipientImgUrl = bundle.getString(RECIPIENT_IMAGE_URL)!!
        val id = bundle.getString(INBOX_UID)
        if (id != null) {
            presenter.inboxId = id
            presenter.inboxExist = true
            presenter.recipientNickname=recipientNickname
            presenter.recipientImageUri=recipientImgUrl
            presenter.getMessagesForThisInbox(id)
            presenter.recipientUid=recipientID
            Log.d("inboxID", "id : $id")
        } else {
            Log.d("inboxID", "checkInbox()")
            checkInbox()
        }
    }

    private fun scrollToBottom() {
        layoutManagerVar.scrollToPosition(recyclerAdapter.getSize() - 1)
    }

    override fun fillAdapterWith(messages: MutableList<Message>) {
        recyclerAdapter.insertMessages(messages)
        recyclerAdapter.notifyDataSetChanged()
    }

    override fun insertNewMessage(message: Message) {
        Log.d("insertNewMessage", "CALLED")
        recyclerAdapter.addNewMessage(message)
        recyclerAdapter.notifyDataSetChanged()
        scrollToBottom()
        if (automaticSeenState == AUTOMATIC_SEEN_ENABLED) {
            presenter.seenLastMessage()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("insertNewMessageOP", "CALLED_onPause ")
        automaticSeenState = AUTOMATIC_SEEN_DISABLED
    }

    override fun onResume() {
        super.onResume()
        Log.d("insertNewMessageOP", "CALLED_onResume")
        automaticSeenState = AUTOMATIC_SEEN_ENABLED
        presenter.setKeyboardListener(inboxMainLayout)

    }

    override fun colapseOptionsMenu() {
        presenter.fileButtonClicked(inboxMessagesLayout, inboxHolderLayout)
    }

    override fun colapseKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun isInputMesageEditTextFocused(): Boolean {
        if (this.currentFocus?.id == messageInput.id) {
            return true
        }
        return false
    }
    override fun fillGifAdapterWith(gifUrls: MutableList<String>) {
        gifAdapter.insertGifs(gifUrls,this)
        gifAdapter.notifyDataSetChanged()
    }
    override fun onGifClick(position: Int) {
        presenter.onGifClicked(position)
    }

    override fun animate(show: Boolean) {

        val view: View = searchText
        if(!show && !iscolapsed){
           // view.animate().translationY(recyclerHolder.height.toFloat()-gifRecycler.height.toFloat()).setDuration(200).start()
            val anim = ValueAnimator.ofInt(view.measuredHeight,0)
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                val layoutParams = view.getLayoutParams()
                layoutParams.height = value
                view.setLayoutParams(layoutParams)
            }
            anim.duration = 200
            anim.start()
            iscolapsed=true
        }
        if(show && iscolapsed){
            val anim = ValueAnimator.ofInt(view.measuredHeight,131)
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                val layoutParams = view.getLayoutParams()
                layoutParams.height = value
                view.setLayoutParams(layoutParams)
            }
            anim.duration = 200
            anim.startDelay=500
            anim.start()
            iscolapsed=true
            iscolapsed=false
            Log.d("animateView","Should Be Shown now")
        }
    }

    override fun gifOPtionsSetVisibility(visibile: Boolean) {
        if(visibile){
            options_gif_holder.visibility=View.VISIBLE
        }else {options_gif_holder.visibility=View.GONE}
    }

    override fun picturesOptionsSetVisibility(visibile: Boolean) {
        if(visibile){
            options_pictures_holder.visibility=View.VISIBLE
        }else {options_pictures_holder.visibility=View.GONE}
    }


}

