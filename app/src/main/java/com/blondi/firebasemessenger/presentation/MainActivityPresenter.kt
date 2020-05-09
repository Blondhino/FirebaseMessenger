package com.blondi.firebasemessenger.presentation

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivityPresenter(context: Context) {
    val firebase = FirebaseAuth.getInstance()
    val context=context
    fun checkLoginStatus():Boolean{
        if(firebase.currentUser!=null)
            return true
        return false
    }

    fun changeActivity(intent: Intent){
        startActivity(context, intent, null)
    }

    interface View {
        fun goToMessages()
        fun goToLoginSignupActivity()
    }

}