package com.tuanquan.chat

import com.tuanquan.chat.constances.Constances.Companion.BASE_URL
import com.tuanquan.chat.notification.ApiServices
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private val retrofit by lazy{
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy{
            retrofit.create(ApiServices::class.java)
        }
    }
}