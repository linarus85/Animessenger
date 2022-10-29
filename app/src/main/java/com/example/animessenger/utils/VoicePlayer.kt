package com.example.animessenger.utils

import android.media.MediaPlayer
import java.io.File

class VoicePlayer {
    private lateinit var mediaPayer: MediaPlayer
    private lateinit var file: File

    fun play(messageKey: String, fileUrl: String, function: () -> Unit) {
        file = File(APP_ACTIVITY.filesDir, messageKey)
        if (file.exists() && file.length() > 0 && file.isFile) {
            startPlay {
                function()
            }
        } else {
            file.createNewFile()
            getFileFromStorage(file, fileUrl) {
                startPlay {
                    function()
                }
            }
        }
    }

    private fun getFileFromStorage(file: File, fileUrl: String, function: () -> Unit) {
        val path = REF_STORAGE.storage.getReferenceFromUrl(fileUrl)
        path.getFile(file)
            .addOnSuccessListener {function() }
            .addOnFailureListener{ showToast(it.message.toString())}
    }

    private fun startPlay(function: () -> Unit) {
        try {
            mediaPayer.setDataSource(file.absolutePath)
            mediaPayer.prepare()
            mediaPayer.start()
            mediaPayer.setOnCompletionListener {
                stop {
                    function()
                }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

     fun stop(function: () -> Unit) {
        try {
            mediaPayer.stop()
            mediaPayer.reset()
            function()
        } catch (e: Exception) {
            showToast(e.message.toString())
            function()
        }
    }

    fun release() {
        mediaPayer.release()
    }
    fun initPlayer(){
        mediaPayer = MediaPlayer()
    }
}