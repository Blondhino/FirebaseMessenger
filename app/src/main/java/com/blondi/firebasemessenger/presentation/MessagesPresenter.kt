package com.blondi.firebasemessenger.presentation

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.blondi.firebasemessenger.common.TOAST_SHOW
import com.blondi.firebasemessenger.common.TOAST_SUCCESS
import com.blondi.firebasemessenger.ui.Activities.MessagesActivity
import com.blondi.firebasemessenger.ui.fragments.NewMessageFragment
import com.blondi.firebasemessenger.ui.fragments.NotificationsFragment
import com.blondi.firebasemessenger.ui.fragments.UsersMessagesFragment
import com.blondi.firebasemessenger.ui.adapters.messagesAdapter
import com.blondi.firebasemessenger.utils.FirebaseMessengerApp
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.blondi.firebasemessenger.utils.ImageResizerCompresser


class MessagesPresenter(context: Context, view: View) {
    val context = context
    val view = view
    val firebase = FirebaseAuth.getInstance()
    val facebook = LoginManager.getInstance()
    val database = FirebaseDatabase.getInstance().getReference()
    val storage = FirebaseStorage.getInstance().getReference()
    lateinit var imageTools: ImageResizerCompresser

    fun logOutUser() {
        firebase.signOut()
        facebook.logOut()
    }

    fun changeActivity(intent: Intent) {
        startActivity(context, intent, null)
    }

    fun checkNickname(nickName: String): Boolean {
        if (nickName.trim().isEmpty()) {
            return false
        }
        return true
    }

    fun setNickname(nickname: String, dialog: Dialog) {
        if (!checkNickname(nickname)) {
            Toast.makeText(
                FirebaseMessengerApp.getAppContext(),
                "NickName should not be empty",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            view.sayHelloToUser(nickname.trim())
            database.child("users").child(firebase.currentUser?.uid.toString())
                .child("nickName")
                .setValue(nickname.trim())
            view.dissmisDialog(dialog)
            view.startProfileImageDialog()
        }
    }

    fun checkNicknameValue() {
        view.disableUIuserInteraction()
        val childRef = database.child("users").child(firebase.currentUser?.uid.toString())
        childRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild("nickName")) {
                    view.enableUIuserInteraction()
                    view.sayHelloToUser(dataSnapshot.child("nickName").value.toString())
                    childRef.removeEventListener(this)
                } else {
                    view.enableUIuserInteraction()
                    view.startNicknameDialog()
                    childRef.removeEventListener(this)
                }
            }

        })
    }

    fun insertFragments(adapter: messagesAdapter) {
        adapter.addFragment(UsersMessagesFragment())
        adapter.addFragment(NewMessageFragment())
        adapter.addFragment(NotificationsFragment())
    }

    fun uploadImageToDatabase(imgURI: Uri) {
        view.toastUserAboutImageUpload(TOAST_SHOW)
        val storageREF = storage.child("profileImages")
            .child(firebase.currentUser?.uid.toString())
        //Compressing
        imageTools = ImageResizerCompresser()
        val data = imageTools.resizeCompress(imgURI, 25, 400)
        storageREF.putBytes(data).addOnSuccessListener {
            storageREF.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                override fun onSuccess(uri: Uri?) {
                    addImageToUserDatabaseProfile(uri!!)
                    view.toastUserAboutImageUpload(TOAST_SUCCESS)
                }

            })
        }

    }

    fun startUploadImageToDatabase() {
        view.startUploadImageToDatabase()
    }

    fun addImageToUserDatabaseProfile(imageURI: Uri) {
        database.child("users").child(firebase.currentUser?.uid.toString())
            .child("imageURI").setValue(imageURI.toString())
    }

    fun openFileSelector() {
        view.openFileSelector()
    }

    fun backPresed(position: Int) {
        if (position > 0) {
            view.swipeTo(0)
        } else {
            view.minimizeApp()
        }
    }


    interface View {
        fun logOut()
        fun goToLoginSignupActivity()
        fun sayHelloToUser(nickname: String)
        fun startNicknameDialog()
        fun dissmisDialog(dialog: Dialog)
        fun startProfileImageDialog()
        fun disableUIuserInteraction()
        fun enableUIuserInteraction()
        fun toastUserAboutImageUpload(state: Int)
        fun openFileSelector()
        fun startUploadImageToDatabase()
        fun swipeTo(position: Int)
        fun minimizeApp()

    }

}