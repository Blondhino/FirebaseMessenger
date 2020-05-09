package com.blondi.firebasemessenger.ui.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.blondi.firebasemessenger.R
import com.blondi.firebasemessenger.common.TAG
import com.blondi.firebasemessenger.ui.adapters.signupLoginAdapter
import com.blondi.firebasemessenger.presentation.LoginSignupPresenter
import com.blondi.firebasemessenger.utils.LoggerHelper
import com.facebook.AccessToken
import com.facebook.CallbackManager
import kotlinx.android.synthetic.main.activity_login_signup.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.android.synthetic.main.login_signup_foother.*
import kotlinx.android.synthetic.main.login_signup_header.*

class LoginSignupActivity : AppCompatActivity(),LoginSignupPresenter.ViewInterface {
    val logger = LoggerHelper()
    val callbackManager = CallbackManager.Factory.create()
    val presenter = LoginSignupPresenter(this,this)
    val viewPagerAdapter =signupLoginAdapter(supportFragmentManager)
    override fun onCreate(savedInstanceState: Bundle?) {
        logger.logMsg(this,"OnCreate ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)
        initUI()
    }

    private fun initFacebook() {
        logger.logMsg(this,"initFacebook")
        facebookLoginButton.setReadPermissions("email", "public_profile")
        facebookLoginButton.setOnClickListener { presenter.facebookButtonClicked() }
        presenter.setupFacebookLogin (facebookLoginButton,callbackManager)
        facebookLoginImage.setOnClickListener { presenter.facebookImageClicked(facebookLoginButton) }
    }

    override fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        presenter.loginToFirebaseWithFacebookToken(token)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun initUI() {
        authenticationAlternativeMsg.text= Html.fromHtml("Already have an account ? go to <b>LOGIN</b>")
        logger.logMsg(this,"initUI")
        setUpViewpager()
        initializeListeners()
        initFacebook()
    }

    private fun initializeListeners() {
        logger.logMsg(this,"initializeListeners")
        loginSignupButton.setOnClickListener { presenter.buttonClicked(loginSignupViewPager.currentItem) }
        authenticationAlternativeMsg.setOnClickListener { presenter.alternativeMsgClicked(loginSignupViewPager.currentItem) }
    }

    private fun setUpViewpager() {
        logger.logMsg(this,"setUpViewpager")
        loginSignupViewPager.adapter=viewPagerAdapter
        setUpViewPagerListener()
        presenter.fillAdapter(viewPagerAdapter)
    }

    private fun setUpViewPagerListener() {
        logger.logMsg(this,"setUpViewPagerListener")
        loginSignupViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {presenter.fragmentChanged(position)}})
    }


    override fun goToMessages() {
        logger.logMsg(this,"goToMessages")
        val intent = Intent(this, MessagesActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        presenter.changeActivity(intent)
        finish()
    }

    override fun changeButtonText(newText: String) {
        logger.logMsg(this,"changeButtonText")
        loginSignupButton.text=newText
    }

    override fun changeHeaderText(newText: String) {
        logger.logMsg(this,"changeHeaderText")
        headerText.text=newText
    }

    override fun changeAuthentiticationAlternativeMsg(newText: String) {
        logger.logMsg(this,"changeAuthentiticationAlternativeMsg")
        authenticationAlternativeMsg.text= Html.fromHtml(newText)
    }


    override fun enableUIuserInteraction() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        loginSignupProgressBar.visibility=View.INVISIBLE
        layoutMask.visibility=View.INVISIBLE
    }

    override fun disableUIuserInteraction() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        layoutMask.visibility=View.VISIBLE
        loginSignupProgressBar.visibility=View.VISIBLE
    }

    override fun catchDataFromSingup() {
        presenter.saveSignUpData(
            signupEmail.text.toString(),
            signupPassword.text.toString(),
            signupRePassword.text.toString())
    }

    override fun catchDataFromLogin() {
       presenter.saveLoginData(
           loginEmail.text.toString(),
           loginPassword.text.toString()
       )
    }

    override fun swipeTo(position: Int) {
        loginSignupViewPager.setCurrentItem(position,true)
    }


}