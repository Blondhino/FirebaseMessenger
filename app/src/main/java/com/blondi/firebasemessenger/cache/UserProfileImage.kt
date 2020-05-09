package com.blondi.firebasemessenger.cache

import android.net.Uri
import android.preference.PreferenceManager
import com.blondi.firebasemessenger.common.USER_IMAGE
import com.blondi.firebasemessenger.utils.FirebaseMessengerApp


object UserProfileImage {
    private fun sharedPrefs() = PreferenceManager.getDefaultSharedPreferences(FirebaseMessengerApp.getAppContext())
    fun saveUsersProfileImage (imageURI : Uri){
        val editor = sharedPrefs().edit()
        editor.putString(USER_IMAGE, imageURI.toString()).apply()
    }
    fun getUsersProfileImage():String? = sharedPrefs().getString(USER_IMAGE, "null")
}
