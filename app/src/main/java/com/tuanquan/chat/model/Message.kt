package com.tuanquan.chat.model

data class Message(
    var message: String? = null,
    var senderId: String? = null,
    var time: Long? = null
)
