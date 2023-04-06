package com.tuanquan.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.tuanquan.chat.ChatActivity
import com.tuanquan.chat.R
import com.tuanquan.chat.adapter.OnlUserAdapter.ViewHolder
import com.tuanquan.chat.databinding.UserItemBinding
import com.tuanquan.chat.model.Message
import com.tuanquan.chat.model.User
import com.tuanquan.chat.util.ItemClickListener
import java.text.SimpleDateFormat
import java.util.*

class OnlUserAdapter(val context: Context, private val userList: ArrayList<User>): RecyclerView.Adapter<ViewHolder>() {

    private val mAuth = FirebaseAuth.getInstance()
    var theLastMessage = "default"

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = userList[position]

        holder.userName.text = item.username.toString()

        if(item.imageUri.equals("default")){
            holder.avatar.setImageResource(R.mipmap.ic_launcher_round)
        }else{
            Glide.with(context).load(item.imageUri).into(holder.avatar)
        }

        holder.setClickListener(object :ItemClickListener{
            override fun onClick(view: View) {
                val intent = Intent(context, ChatActivity::class.java)

                intent.putExtra("name", item.username)
                intent.putExtra("uid", item.uid)
                intent.putExtra("imageUrl", item.imageUri)
                context.startActivity(intent)
            }
        })

        lastMess(mAuth.currentUser!!.uid, holder.lastMes, item, holder.timeMes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = UserItemBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(binding)
    }

    class ViewHolder(binding: UserItemBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        private lateinit var clickListener: ItemClickListener

        val userName = binding.username
        val avatar = binding.avatar
        val lastMes = binding.lastestMes
        val timeMes = binding.lastestMessTime

        fun setClickListener(itemClickListener: ItemClickListener) {
            this.clickListener = itemClickListener
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            clickListener.onClick(view)
        }
    }

    private fun lastMess(uid: String, txt_lastMes: TextView,
                         item: User, txt_time: TextView){
        val mDbRef = Firebase.database.getReference("chats").child(uid+item.uid)
            .child("message").orderByKey().limitToLast(1)

        var time: Long? = 0

        mDbRef.addValueEventListener(object: ValueEventListener{
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val message = snapshot.getValue<Map<String, Message>>()
                    Log.d("Database_TUANQUAN", message.toString())
                    message?.values!!.forEach {
                        if (it.senderId == uid) {
                            theLastMessage = "You: " + it.message.toString()
                        } else if (it.senderId == item.uid) {
                            theLastMessage =
                                item.username + ": " + it.message.toString()
                        }
                        time = it.time
                    }
                }

                when (theLastMessage) {
                    "default" -> txt_lastMes.text = "No message"
                    else -> txt_lastMes.text = theLastMessage
                }
                if (time!! >= System.currentTimeMillis()/1000 &&
                    time!! <= System.currentTimeMillis()/1000 + 60 * 60 * 24 * 60
                ) {
                    txt_time.text = SimpleDateFormat("HH:mm")
                        .format(Date(time!!.times(1000)))
                } else {
                    txt_time.text = SimpleDateFormat("dd-MM")
                        .format(Date(time!!.times(1000)))
                }

                theLastMessage = "default"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}
