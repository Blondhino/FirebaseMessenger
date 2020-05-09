package com.blondi.firebasemessenger.ui.Activities

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blondi.firebasemessenger.R
import com.blondi.firebasemessenger.cache.UserProfileImage
import com.blondi.firebasemessenger.common.PICK_IMAGE_REQUEST
import com.blondi.firebasemessenger.ui.adapters.messagesAdapter
import com.blondi.firebasemessenger.presentation.MessagesPresenter
import com.blondi.firebasemessenger.ui.dialogs.NicknameDialog
import com.blondi.firebasemessenger.ui.dialogs.ProfileImageDialog
import com.blondi.firebasemessenger.utils.LoggerHelper
import com.blondi.firebasemessenger.utils.PrettyToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.messages_header_layout.*

class MessagesActivity : AppCompatActivity(), MessagesPresenter.View {
    val loger = LoggerHelper()
    lateinit var nicknameDialog: NicknameDialog
    lateinit var profileImageDialog: ProfileImageDialog
    lateinit var imageUri: Uri
    val presenter = MessagesPresenter(this, this)
    val viewPagerAdapter = messagesAdapter(supportFragmentManager)
    override fun onCreate(savedInstanceState: Bundle?) {
        loger.logMsg(this, "On create")
        super.onCreate(savedInstanceState)
        setContentView(com.blondi.firebasemessenger.R.layout.activity_messages)
        initUI()
        checkForNickname()
    }

    private fun checkForNickname() {
        presenter.checkNicknameValue()
    }

    private fun initUI() {
        loger.logMsg(this, "InitUI")
        logOutButton.setOnClickListener { logOut() }
        setupBottomNAvigation()
        setupViewPager()
    }

    private fun setupBottomNAvigation() {
         val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item->
            when(item.itemId){
                R.id.navigation_home -> {
                    println("home pressed")
                   swipe(0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_new_msg -> {
                    println("map pressed")
                    swipe(1)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    println("cart pressed")
                    swipe(2)
                    return@OnNavigationItemSelectedListener true
                }
            }

            false

        }
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private fun setupViewPager() {
        messagesViewPager.adapter = viewPagerAdapter
        presenter.insertFragments(viewPagerAdapter)
    }

    fun swipe(position: Int) {
        messagesViewPager.setCurrentItem(position, true)
    }

    override fun openFileSelector() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun logOut() {
        loger.logMsg(this, "Logging out...")
        presenter.logOutUser()
        loger.logMsg(this, "logout successful")
        goToLoginSignupActivity()
        finish()
    }

    override fun goToLoginSignupActivity() {
        loger.logMsg(this, "goToLoginSignupActivity")
        val intent = Intent(this, LoginSignupActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        presenter.changeActivity(intent)
    }

    override fun sayHelloToUser(nickname: String) {
        Toast.makeText(this, "Hello $nickname \ud83d\ude0e", Toast.LENGTH_LONG).show()
    }

    override fun startNicknameDialog() {
        nicknameDialog = NicknameDialog(this, presenter)
        nicknameDialog.startDialog()
    }

    override fun dissmisDialog(dialog: Dialog) {
        dialog.dismiss()
    }

    override fun disableUIuserInteraction() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        layoutMaskMessages.visibility = View.VISIBLE
        progressBarMessages.visibility = View.VISIBLE

    }

    override fun enableUIuserInteraction() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        layoutMaskMessages.visibility = View.INVISIBLE
        progressBarMessages.visibility = View.INVISIBLE
    }

    override fun startProfileImageDialog() {
        profileImageDialog = ProfileImageDialog(this, presenter)
        profileImageDialog.startDialog()
    }

    override fun startUploadImageToDatabase() {
        presenter.uploadImageToDatabase(imageUri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.data != null
        ) {
            imageUri = data.data!!
            profileImageDialog.setImageURI(data.data!!)
            UserProfileImage.saveUsersProfileImage(data.data!!)
        }
    }

    override fun toastUserAboutImageUpload(state: Int) {
        PrettyToast(messagesActivityView).showToast(state)
    }

    override fun onBackPressed() {
        presenter.backPresed(messagesViewPager.currentItem)
    }

    override fun swipeTo(position: Int) {
        swipe(position)
    }

    override fun minimizeApp() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(startMain)
    }
}