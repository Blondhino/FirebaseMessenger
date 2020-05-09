package com.blondi.firebasemessenger.utils

import android.content.Context
import android.util.Log
import android.widget.Toast


class LoggerHelper {
    fun logMsg(context : Context, msg :String){
        Log.d("logger: "+context.javaClass.simpleName,msg)
    }

}