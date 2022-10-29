package com.example.animessenger.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

const val READ_CONTACTS = Manifest.permission.READ_CONTACTS
const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE


const val PERMISSION_REQ = 200

fun checkPermission(permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= 23 &&
        ContextCompat.checkSelfPermission(APP_ACTIVITY, permission) !=
        PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(APP_ACTIVITY, arrayOf(permission),
            PERMISSION_REQ)
        false
    } else true
}


fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return  timeFormat.format(time)
}