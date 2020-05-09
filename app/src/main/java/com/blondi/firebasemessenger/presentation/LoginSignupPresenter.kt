package com.blondi.firebasemessenger.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.blondi.firebasemessenger.ui.fragments.LoginFragment
import com.blondi.firebasemessenger.ui.fragments.SignUpFragment
import com.blondi.firebasemessenger.ui.adapters.signupLoginAdapter
import com.blondi.firebasemessenger.utils.FirebaseMessengerApp
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginSignupPresenter(context: Context, view : ViewInterface) {
    private val analyitics = FirebaseAnalytics.getInstance(FirebaseMessengerApp.getAppContext())
    val database = FirebaseDatabase.getInstance().getReference()
    private val context=context
    private val view : ViewInterface? = view
    private val firebase = FirebaseAuth.getInstance()
    private lateinit var  email: String
    private lateinit var mEmail :String
    private lateinit var mPassword: String
    private lateinit var mRePassword: String

    fun changeActivity(intent: Intent){
        startActivity(context,intent,null)

    }

    fun fillAdapter(adapter : signupLoginAdapter) {
        adapter.addFragment(SignUpFragment())
        adapter.addFragment(LoginFragment())
    }

    fun fragmentChanged(position: Number) {
        if(position==0){
            view?.changeButtonText("Sign up")
            view?.changeHeaderText("sign up")
            view?.changeAuthentiticationAlternativeMsg("Already have an account ? go to <b>LOGIN</b>")
        }
        if(position==1){
            view?.changeButtonText("Login")
            view?.changeHeaderText("Login")
            view?.changeAuthentiticationAlternativeMsg("Don't have an account ? go to <b>SIGN UP</b>")
        }
    }

    fun setUiInProgress(state :Boolean){
        if(state){
            view?.disableUIuserInteraction()
        }else{
            view?.enableUIuserInteraction()
        }
    }
    fun buttonClicked(position :Int) {
        setUiInProgress(true)
        getData(position)
        if (position==0){createNewUser()}
        if(position==1){LoginUser()}
    }
    private fun LoginUser() {
        if(mEmail.trim().isEmpty()|| mPassword.trim().isEmpty()){
            Toast.makeText(FirebaseMessengerApp.getAppContext(),"Fields should not be empty",Toast.LENGTH_LONG).show()
            setUiInProgress(false) }
            if(mEmail.trim().isNotEmpty()&&mPassword.trim().isNotEmpty()){
            firebase.signInWithEmailAndPassword(mEmail.trim(),mPassword.trim()).addOnCompleteListener {task ->
                if(task.isSuccessful ){
                    setUiInProgress(false)
                    Toast.makeText(FirebaseMessengerApp.getAppContext(),"successfully logged in",Toast.LENGTH_LONG).show()
                    analyitics.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle())
                    view?.goToMessages()
                }
                else{
                    Toast.makeText(FirebaseMessengerApp.getAppContext(),task.exception?.message.toString(),Toast.LENGTH_LONG).show()
                    setUiInProgress(false)
                }


            }
            }

    }
    private fun createNewUser() {
        if(mEmail.trim().isEmpty()|| mPassword.trim().isEmpty() || mRePassword.trim().isEmpty() ){
            Toast.makeText(FirebaseMessengerApp.getAppContext(),"Fields should not be empty",Toast.LENGTH_LONG).show()
            setUiInProgress(false)
        }
        if(mEmail.trim().isNotEmpty()&&mPassword.trim().isNotEmpty()&& mRePassword.trim().isNotEmpty()){
            if(mPassword.trim()!=mRePassword.trim()){Toast.makeText(FirebaseMessengerApp.getAppContext(),"Passwords don't match",Toast.LENGTH_LONG).show()
            setUiInProgress(false)
            }
            else {
                firebase.createUserWithEmailAndPassword(mEmail.trim(),mPassword.trim()).addOnCompleteListener {task ->
                    if(task.isSuccessful ){
                        setUiInProgress(false)
                        Toast.makeText(FirebaseMessengerApp.getAppContext(),"successfully signed up",Toast.LENGTH_LONG).show()
                        analyitics.logEvent(FirebaseAnalytics.Event.SIGN_UP, Bundle())
                        database.child("users").child(firebase.currentUser?.uid.toString()).setValue(1)
                        val userRef = database.child("users").child(firebase.currentUser?.uid.toString())
                        userRef.child("uid").setValue(firebase.currentUser?.uid.toString())
                        view?.goToMessages()

                    }
                    else{
                        Toast.makeText(FirebaseMessengerApp.getAppContext(),task.exception?.message.toString(),Toast.LENGTH_LONG).show()
                        setUiInProgress(false)
                    }


                }
            }
        }
    }
    fun getData(position: Int){
        if (position==0)
            view?.catchDataFromSingup()
        else{
            view?.catchDataFromLogin()
        }
    }
    fun saveSignUpData(email :String,password:String, rePassword : String){
        mEmail=email
        mPassword=password
        mRePassword=rePassword
    }
    fun saveLoginData(email :String,password:String){
        mEmail=email
        mPassword=password
    }
    fun setupFacebookLogin(
        facebookLoginButton: LoginButton?,
        callbackManager: CallbackManager
    ) {
        facebookLoginButton?.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("FACEBOOK", "facebook:onSuccess:$loginResult")
                analyitics.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle())
                view?.handleFacebookAccessToken(loginResult.accessToken)
            }
            override fun onCancel() {
                Log.d("FACEBOOK", "facebook:onCancel")
            }
            override fun onError(error: FacebookException) {
                Log.d("FACEBOOK", "facebook:onError", error)

            }
        })
    }
    fun loginToFirebaseWithFacebookToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebase.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    view?.enableUIuserInteraction()
                    firstCheckIfUserExist()
                    view?.goToMessages()
                } else {
                    view?.enableUIuserInteraction()
                    Log.w("FACEBOOK", "signInWithCredential:failure", task.exception)
                    Toast.makeText(FirebaseMessengerApp.getAppContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }
    private fun firstCheckIfUserExist() {
        val useresRef = database.child("users")
        useresRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.hasChild(firebase.currentUser?.uid.toString())){ }
                else{
                    database.child("users").child(firebase.currentUser?.uid.toString()).setValue(1)
                    val userRef = database.child("users").child(firebase.currentUser?.uid.toString())
                    userRef.child("uid").setValue(firebase.currentUser?.uid.toString())
                }
            }

        })
    }
    fun facebookButtonClicked() {
        view?.disableUIuserInteraction()
    }
    fun alternativeMsgClicked(currentItem: Int) {
        if(currentItem==0){
            view?.swipeTo(1)
        }
        else view?.swipeTo(0)
    }
    fun facebookImageClicked(facebookLoginButton: LoginButton?) {
        facebookLoginButton?.performClick()
    }
    interface ViewInterface{
        fun goToMessages()
        fun changeButtonText(newText : String)
        fun changeHeaderText(newText : String)
        fun changeAuthentiticationAlternativeMsg(newText : String)
        fun enableUIuserInteraction()
        fun disableUIuserInteraction()
        fun catchDataFromSingup()
        fun catchDataFromLogin()
        fun handleFacebookAccessToken(token: AccessToken)
        fun swipeTo(position : Int)
        }
}

