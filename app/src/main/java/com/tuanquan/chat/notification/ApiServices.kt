package com.tuanquan.chat.notification

import com.tuanquan.chat.constances.Constances.Companion.CONTENT_TYPE
import com.tuanquan.chat.constances.Constances.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiServices {
    @Headers(value=[
        "Authorization: key = $SERVER_KEY",
        "Content-Type:$CONTENT_TYPE"
    ])
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification)
    : Call<ResponseBody>
}