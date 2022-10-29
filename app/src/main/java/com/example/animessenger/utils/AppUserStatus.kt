package com.example.animessenger.utils

enum class AppUserStatus(val status:String) {
    ONLINE("online"),
    OFFLINE("был недавно"),
    TYPING("печатает");

    companion object{
        fun updateStatus(appStatus:AppUserStatus){
            if (auth.currentUser!=null){
                REF_DATABASE.child(NODE_USERS).child(UID).child(CHILD_STATUS)
                    .setValue(appStatus.status)
                    .addOnSuccessListener { USER.status = appStatus.status }
                    .addOnFailureListener{ showToast(it.message.toString())}
            }
        }
    }
}