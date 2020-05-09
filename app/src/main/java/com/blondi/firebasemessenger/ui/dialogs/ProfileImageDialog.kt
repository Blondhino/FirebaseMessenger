package com.blondi.firebasemessenger.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import com.blondi.firebasemessenger.common.PICK_IMAGE_REQUEST
import com.blondi.firebasemessenger.presentation.MessagesPresenter
import com.blondi.firebasemessenger.utils.FirebaseMessengerApp
import de.hdodenhof.circleimageview.CircleImageView

class ProfileImageDialog(context : Context, presenter :MessagesPresenter){
    val profileImageDialog = Dialog(context)
    val presenter=presenter
    fun startDialog(){
        profileImageDialog.setContentView(com.blondi.firebasemessenger.R.layout.dialog_profile_image)
        val confirm = profileImageDialog.findViewById<Button>(com.blondi.firebasemessenger.R.id.confirm_button_image)
        val cancel = profileImageDialog.findViewById<Button>(com.blondi.firebasemessenger.R.id.cancel_button_image)
        val profileImage = profileImageDialog.findViewById<CircleImageView>(com.blondi.firebasemessenger.R.id.profileImage)
        profileImageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        profileImage.setOnClickListener { openFileSelector() }
        cancel.setOnClickListener { profileImageDialog.dismiss() }
        confirm.setOnClickListener { presenter.startUploadImageToDatabase()
            profileImageDialog.dismiss()
        }
        profileImageDialog.setCancelable(false)
        profileImageDialog.show()
    }

    private fun openFileSelector() {
       presenter.openFileSelector()
    }
    fun setImageURI(uri:Uri){
        profileImageDialog.setContentView(com.blondi.firebasemessenger.R.layout.dialog_profile_image)
        val confirm = profileImageDialog.findViewById<Button>(com.blondi.firebasemessenger.R.id.confirm_button_image)
        val cancel = profileImageDialog.findViewById<Button>(com.blondi.firebasemessenger.R.id.cancel_button_image)
        val profileImage = profileImageDialog.findViewById<CircleImageView>(com.blondi.firebasemessenger.R.id.profileImage)
        profileImage.setImageURI(uri)
        profileImage.setOnClickListener { openFileSelector() }
        cancel.setOnClickListener { profileImageDialog.dismiss() }
        confirm.setOnClickListener { presenter.startUploadImageToDatabase()
            profileImageDialog.dismiss()
        }

    }

}
