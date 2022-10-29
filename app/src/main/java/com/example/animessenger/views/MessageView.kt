package com.example.animessenger.views

interface MessageView {
    val id: String
    val fromUser: String
    val timeStamp: String
    val fileUrl: String
    val text: String

    companion object{
        val MESSAGE_VOICE:Int
            get() = 0
    }
    fun getTypeView():Int
}