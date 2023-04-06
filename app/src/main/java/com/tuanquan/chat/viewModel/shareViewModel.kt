package com.tuanquan.chat.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tuanquan.chat.model.User

class shareViewModel: ViewModel() {

    lateinit var userData: User

    val currentUser = FirebaseAuth.getInstance().currentUser

    val mDfRefOfUser = Firebase.database.getReference("users").child(currentUser!!.uid)

    val mDfRefOfLastMess = Firebase.database.getReference("chats")

    init {
        mDfRefOfUser.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children){
                    userData = postSnapshot.getValue(User::class.java)!!
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}