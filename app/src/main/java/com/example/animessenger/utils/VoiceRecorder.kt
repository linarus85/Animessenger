package com.example.animessenger.utils

import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class VoiceRecorder {
        private val mediaRecoder = MediaRecorder()
        private lateinit var file: File
        private lateinit var messageKey: String

        fun startRecorder(mKey: String) {
            try {
                messageKey = mKey
                createFileRecorder()
                prepareMediaRecoder()
                mediaRecoder.start()
            } catch (e: Exception) {
                showToast(e.message.toString())
            }

        }

        private fun prepareMediaRecoder() {
            mediaRecoder.reset()
            mediaRecoder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            mediaRecoder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            mediaRecoder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            mediaRecoder.setOutputFile(file.absolutePath)
            mediaRecoder.prepare()
        }

        private fun createFileRecorder() {
            file = File(APP_ACTIVITY.filesDir, messageKey)
            file.createNewFile()
        }

        fun stopRecorder(onSuccess: (files: File, messageKey: String) -> Unit) {
            try {
                mediaRecoder.stop()
                onSuccess(file, messageKey)
            } catch (e: Exception) {
                showToast(e.message.toString())
                file.delete()
            }
        }

        fun releaseRecorder() {
            try {
                mediaRecoder.release()
            } catch (e: Exception) {
                showToast(e.message.toString())
            }
        }
}