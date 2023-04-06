package com.tuanquan.chat.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tuanquan.chat.MainActivity
import com.tuanquan.chat.R
import kotlin.random.Random

const val CHANNEL_ID = "com.tuanquan.chitchatchup"
const val CHANNEL_NAME = "chitchapchut"

class MyFirebaseMessagingService: FirebaseMessagingService() {

    companion object{
        var sharePreds: SharedPreferences? = null
        var token: String?
        get(){
            return sharePreds?.getString("token", "")
        }
        set(value){
            sharePreds?.edit()?.putString("token", value)?.apply()
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        token = p0
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val contentIntent = Intent(this, MainActivity::class.java)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            createNotificationChannel(notificationManager)

        }
        contentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, contentIntent, FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH).apply {
            description = "Chit Chat Chup Description"
            enableLights(false)
        }
        notificationManager.createNotificationChannel(channel)
    }
}

  