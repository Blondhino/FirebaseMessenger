package com.blondi.firebasemessenger.ui.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.blondi.firebasemessenger.presentation.MainActivityPresenter
import com.blondi.firebasemessenger.utils.LoggerHelper


class MainActivity : AppCompatActivity(),MainActivityPresenter.View  {

    private val logger =LoggerHelper()
    val presenter = MainActivityPresenter(this)
    override fun onStart() {
        super.onStart()
        logger.logMsg(this,"OnCreate")
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        logger.logMsg(this,"Checking Login status...")
        val isLogged :Boolean = presenter.checkLoginStatus()
        if(isLogged){logger.logMsg(this,"login success")
        goToMessages()}
        else{logger.logMsg(this,"login failed")
            goToLoginSignupActivity()
        }
    }
    override fun goToMessages() {
        logger.logMsg(this,"goToMessages")
        val intent = Intent(this, MessagesActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        presenter.changeActivity(intent)
        finish()
    }

    override fun goToLoginSignupActivity() {
        logger.logMsg(this,"goToLoginSignupActivity")
        val intent = Intent(this, LoginSignupActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        presenter.changeActivity(intent)
        finish()
    }

}
