package com.blondi.firebasemessenger.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
@IgnoreExtraProperties
class Inbox(
    var uid : String?=null,
    var recipientNickname: String?=null,
    var recipientUID: String?=null,
    var lastMessage : Message?=null,
    var lastChangeTimeStamp :Long?=null,
    var inboxImage : String?=null
)
