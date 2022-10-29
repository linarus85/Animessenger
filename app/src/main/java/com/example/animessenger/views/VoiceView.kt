package com.example.animessenger.views

class VoiceView(
    override val id: String,
    override val fromUser: String,
    override val timeStamp: String,
    override val fileUrl: String,
    override val text: String = ""
) :MessageView {
    override fun getTypeView(): Int {
        return  MessageView.MESSAGE_VOICE
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }

}