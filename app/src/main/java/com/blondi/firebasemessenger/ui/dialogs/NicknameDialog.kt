package com.blondi.firebasemessenger.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.MessagePattern
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import com.blondi.firebasemessenger.R
import com.blondi.firebasemessenger.presentation.MessagesPresenter

class NicknameDialog(context : Context, presenter :MessagesPresenter) {
    val nicknameDialog = Dialog(context)
    val presenter = presenter
    fun startDialog(){
        nicknameDialog.setContentView(R.layout.dialog_nickname)
        val submitButton = nicknameDialog.findViewById<Button>(R.id.nicknameSubmitBtn)
        val editText = nicknameDialog.findViewById<EditText>(R.id.nickNameEditTex)
        nicknameDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(insertedText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(presenter.checkNickname(insertedText.toString())){
                    //submitButton.background=getDrawable(R.drawable.rounded_button_enabled)
                    submitButton.setBackgroundResource(R.drawable.rounded_button_enabled)
                }
                else{
                    submitButton.setBackgroundResource(R.drawable.rounded_button_disabled)
                }
            }
        })

        submitButton.setOnClickListener {presenter.setNickname(editText.text.toString(),nicknameDialog)}
        nicknameDialog.setCancelable(false)
        nicknameDialog.show()
    }

}