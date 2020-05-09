package com.blondi.firebasemessenger.ui.fragments.fragment_presentation

import com.blondi.firebasemessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot


class NewMessageFragmentPresenter(view: View) {
    val view = view
    val database = FirebaseDatabase.getInstance().getReference()
    val firebase = FirebaseAuth.getInstance()
    var users = mutableListOf<User>()

    fun searchUsersBy(nickName: String){
        if(nickName.isNotEmpty()){
    val query =database
        .child("users")
        .orderByChild("nickName")
        .startAt(nickName)
        .endAt(nickName + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    users.clear()
                         users =dataSnapshot.children.mapNotNullTo(users){
                        it.getValue<User>(User::class.java)     }
                    view.insertSearchResultToRecycler(users)
                    query.removeEventListener(this)
                }else{
                    users.clear()
                    view.insertSearchResultToRecycler(users)
                    query.removeEventListener(this)
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        }) }
    else{users.clear()
        view.insertSearchResultToRecycler(users)
        }
    }
    fun sendEmptyResult(){
    users.clear()
    view.insertSearchResultToRecycler(users)
}
    interface View{
        fun insertSearchResultToRecycler(users : MutableList<User>)
    }

}