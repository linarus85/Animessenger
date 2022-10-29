package com.example.animessenger.utils

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.animessenger.MainActivity
import com.example.animessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

lateinit var APP_ACTIVITY: MainActivity
lateinit var auth: FirebaseAuth
lateinit var REF_DATABASE: DatabaseReference
lateinit var REF_STORAGE: StorageReference
lateinit var USER: User
lateinit var UID: String

const val TYPE_TEXT = "text"
const val TYPE_IMAGE = "image"
const val TYPE_VOICE = "voice"
const val TYPE_CHAT = "chat"
const val TYPE_GROUP = "group"
const val TYPE_CHANNEL = "channel"

const val FILE_REQUEST_CODE = 301

const val NODE_USERS = "users"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"
const val NODE_MAIN = "main"
const val NODE_GROUP = "group"
const val NODE_MEMBERS = "members"
const val NODE_MESSAGES = "messages"
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_NAME = "name"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATUS = "status"
const val CHILD_TEXT = "text"
const val CHILD_TYPE = "type"
const val CHILD_FROM_USER = "fromUser"
const val CHILD_TIME = "timeStamp"
const val CHILD_FILE_URI = "fileUrl"
const val CHILD_GROUP_IMAGE = "group_image"
const val USER_CREATOR = "creator"
const val USER_ADMIN = "admin"
const val USER_MEMBER = "member"

const val STORAGE_PROFILE_IMAGE = "profile_image"
const val STORAGE_FILES = "files"

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_LONG).show()
}

fun initFirebase() {
    auth = FirebaseAuth.getInstance()
    REF_DATABASE = FirebaseDatabase
        .getInstance("https://animessenger-e5ca1-default-rtdb.europe-west1.firebasedatabase.app")
        .getReference()
    USER = User()
    UID = auth.currentUser?.uid.toString()
    REF_STORAGE = FirebaseStorage.getInstance().reference
}



