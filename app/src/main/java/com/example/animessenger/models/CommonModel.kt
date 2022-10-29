package com.example.animessenger.models

data class CommonModel(
    val id: String = "",
    var username: String = "",
    var bio: String = "",
    var name: String = "",
    var status: String = "",
    var photoUrl: String = "Empty",
    var phone: String = "",

    var text: String = "",
    var type: String = "",
    var fromUser: String = "",
    var timeStamp: Any = "",
    var fileUrl: String = "empty",
    var lastMessage: String = "",
    var isChoice: Boolean = false,


    ) {
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }
}
