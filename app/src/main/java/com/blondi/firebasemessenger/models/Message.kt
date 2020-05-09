package com.blondi.firebasemessenger.models

data class Message(
    val senderUID:String?=null,
    var message:String?=null,
    var messageType :String ?=null,
    var shouldBeBold : Boolean?=null,
    var seenStatus:Boolean?=null,
    var lastChangeTimeStamp: Long?=null,
    var messageId:String?=null
)