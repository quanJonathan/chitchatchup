package com.tuanquan.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.tuanquan.chat.adapter.OnlUserAdapter
import com.tuanquan.chat.databinding.ActivityMainBinding
import com.tuanquan.chat.model.User
import com.tuanquan.chat.notification.MyFirebaseMessagingService
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyFirebaseMessagingService.sharePreds = getSharedPreferences("sharePref", Context.MODE_PRIVATE)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(){
            if (it.isSuccessful){
                return@addOnCompleteListener
            }else{
                Log.d("FIREFAIL", "failed")
            }

            Log.d("FIRETOKEN", it.result.toString())
            MyFirebaseMessagingService.token = it.result
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.main_toolbar)

        toolbar.inflateMenu(R.menu.menu_main)

        mAuth = FirebaseAuth.getInstance()



        mDbRef = Firebase.database.reference

        val userList = ArrayList<User>()

        val adapter = OnlUserAdapter(this, userList)

        binding.onlUserRecyclerView.adapter = adapter

        findViewById<CircleImageView>(R.id.avatar).setOnClickListener{
            startActivity(Intent(this, UserSettingActivity::class.java))
        }

        mDbRef.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapShot in snapshot.children){
                    val currentUser = postSnapShot.getValue<User>()
                    userList.add(currentUser!!)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        toolbar.setOnMenuItemClickListener { item ->
            if(item.itemId == R.id.log_out){
                mAuth.signOut()
                finish()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
            false
        }
    }
}