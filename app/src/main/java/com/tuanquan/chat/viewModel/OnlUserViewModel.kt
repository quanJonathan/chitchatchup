package com.tuanquan.chat.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.tuanquan.chat.model.User

class OnlUserViewModel : ViewModel(){
    private val mDbRef = Firebase.database.reference

    private var _onlUserList: MutableLiveData<List<User>> = getAllOnlUser(mDbRef)
    val onlUserList: LiveData<List<User>> get() = _onlUserList

    private fun getAllOnlUser(mDbRef: DatabaseReference): MutableLiveData<List<User>> {
        val tempList = mutableListOf<User>()

        mDbRef.child("users").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapShot in snapshot.children){
                    val currentUser = postSnapShot.getValue<User>()
                    tempList.add(currentUser!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return MutableLiveData(tempList)
    }


}