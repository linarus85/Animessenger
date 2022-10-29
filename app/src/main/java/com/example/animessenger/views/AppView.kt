package com.example.animessenger.views

import com.example.animessenger.models.CommonModel
import com.example.animessenger.utils.TYPE_VOICE

class AppView {
    companion object {
        fun getView(message: CommonModel): MessageView {
            return when (message.type) {
                TYPE_VOICE -> VoiceView(
                    message.id,
                    message.fromUser,
                    message.timeStamp.toString(),
                    message.fileUrl,
                )
                else-> VoiceView(
                    message.id,
                    message.fromUser,
                    message.timeStamp.toString(),
                    message.fileUrl,
                )
            }
        }
    }
}