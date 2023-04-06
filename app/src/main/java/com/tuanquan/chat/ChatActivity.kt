@file:Suppress("BlockingMethodInNonBlockingContext")

package com.tuanquan.chat

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.tuanquan.chat.adapter.MessageAdapter
import com.tuanquan.chat.databinding.ActivityChatBinding
import com.tuanquan.chat.model.Message
import com.tuanquan.chat.notification.NotificationData
import com.tuanquan.chat.notification.PushNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatActivity : AppCompatActivity() {
    private var senderRoom: String? = null
    private var receiverRoom: String? = null

    private lateinit var binding: ActivityChatBinding

    private lateinit var senderUid: String
    private lateinit var receiverUid: String
    private var notify = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(findViewById(R.id.main_toolbar))

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        val username = intent.getStringExtra("name")
        receiverUid = intent.getStringExtra("uid").toString()
        val imageUrl = intent.getStringExtra("imageUrl")

        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

        val messageList = ArrayList<Message>()

        val adapter = MessageAdapter(messageList)

        binding.chatRecyclerView.adapter = adapter

        binding.username.text = username

        getMessage(senderRoom!!, messageList, binding.chatRecyclerView.adapter as MessageAdapter)

        binding.btnSend.setOnClickListener {
            notify = true
            if(!TextUtils.isEmpty(binding.message.text)) {
                val message = binding.message.text.toString()
                val messageObject = Message(message, senderUid, System.currentTimeMillis() / 1000)

                sendMessage(senderRoom!!, receiverRoom!!, messageObject)

                binding.message.setText("")

                binding.chatRecyclerView
                    .smoothScrollToPosition((binding.chatRecyclerView.adapter as MessageAdapter).itemCount)

                val topic = "topics/$senderUid"
                PushNotification(NotificationData(username!!, message), topic).also {
                    sendNotification(it)
                }
            }
        }
    }

    private fun sendMessage(senderRoom: String, receiverRoom: String, message: Message){
        val mDbRef = Firebase.database.reference

        mDbRef.child("chats").child(senderRoom)
            .child("message").push()
            .setValue(message).addOnSuccessListener {
                mDbRef.child("chats").child(receiverRoom).child("message").push()
                    .setValue(message)
            }
    }

    private fun getMessage(senderRoom: String,
                           messageList: ArrayList<Message>,
                           adapter: MessageAdapter){

        val mDbRef = Firebase.database.reference
        mDbRef.child("chats")
            .child(senderRoom)
            .child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                        //Log.d("MESSAGE-TUANQUAN", messageList.toString())
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })

    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch{
        val response = RetrofitInstance.api.postNotification(notification)
        response.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Log.d("SENDRESULTSUCCESS", "Response ${Gson().toJson(response)}")
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.d("SENDRESULTERROR", "Failed")
            }
        })
    }
}