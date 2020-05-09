package com.blondi.firebasemessenger.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
@IgnoreExtraProperties
class User(
    val uid : String?=null,
    val chatRooms: List<String>?=null,
    var imageURI : String?=null,
    var nickName : String?=null
)
