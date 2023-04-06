package com.tuanquan.chat.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.tuanquan.chat.R
import com.tuanquan.chat.model.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_SEND = 1
    private val ITEM_RECEIVE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == ITEM_SEND){
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.send_message, parent, false)

            SendViewHolder(view)

        }else{
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.receive_message, parent, false)
            ReceiveViewHolder(view)
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if (holder.javaClass == SendViewHolder::class.java) {
            val viewHolder = holder as SendViewHolder

            viewHolder.sendMessage.text = currentMessage.message.toString()
        }
        else {
            val viewHolder = holder as ReceiveViewHolder

            viewHolder.itemView.setOnClickListener {
                if (viewHolder.timeOfMessage.visibility == View.GONE) {
                    viewHolder.timeOfMessage.visibility = View.VISIBLE
                    val timeNow = SimpleDateFormat("dd-MM-yyyy")
                        .format(Date(System.currentTimeMillis()))
                    val messageTime = SimpleDateFormat("dd-MM-yyyy")
                        .format(Date(currentMessage.time?.times(1000) ?: 0))
                    if (timeNow.equals(messageTime)) {
                        viewHolder.timeOfMessage.visibility = View.VISIBLE
                        viewHolder.timeOfMessage.text = SimpleDateFormat("HH:mm")
                            .format(Date(currentMessage.time?.times(1000) ?: 0))

                    } else {
                        viewHolder.timeOfMessage.visibility = View.VISIBLE
                        viewHolder.timeOfMessage.text = SimpleDateFormat("dd-MM-yyyy HH:mm")
                            .format(Date(currentMessage.time?.times(1000) ?: 0))
                    }
                } else {
                    viewHolder.timeOfMessage.visibility = View.GONE
                }

            }
            viewHolder.receiveMessage.text = currentMessage.message.toString()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        return if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            ITEM_SEND
        }else{
            ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int = messageList.size

    class SendViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val sendMessage:TextView = item.findViewById(R.id.send_message)

    }

    class ReceiveViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val receiveMessage:TextView = item.findViewById(R.id.receive_message)
        val timeOfMessage:TextView = item.findViewById(R.id.time_mess)
    }

}