package com.blondi.firebasemessenger.utils

import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils.getDrawable
import com.blondi.firebasemessenger.R
import com.blondi.firebasemessenger.common.TOAST_SHOW
import com.blondi.firebasemessenger.common.TOAST_SUCCESS
import kotlinx.android.synthetic.main.toast_layout.*

class PrettyToast(view:View) {
    val view=view
    val toast_message=view.findViewById<TextView>(R.id.toast_message)
    val toastHolder=view.findViewById<RelativeLayout>(R.id.toastHolder)
    val toastUpload = view.findViewById<RelativeLayout>(R.id.toastUpload)
    val toast_proggresBar = view.findViewById<ProgressBar>(R.id.toast_proggresBar)
    fun showToast(state: Int){
        if(state== TOAST_SHOW){
            toast_message.text="Uploading your image in proggress..."
            toastHolder.setBackgroundResource(R.drawable.rounded_toast)
            toastUpload.visibility= View.VISIBLE
            toast_proggresBar.visibility= View.VISIBLE
        }
        if(state== TOAST_SUCCESS){toast_proggresBar.visibility= View.GONE;
            toastHolder.setBackgroundResource(R.drawable.rounded_toast_suc)
            toast_message.text="Your image has been uploaded successfully"
            val handler = Handler()
            handler.postDelayed({toastUpload.visibility= View.GONE},1300)
        }
    }
}