package com.example.animessenger.fragments

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.animessenger.R
import com.example.animessenger.adapter.ChatAdapter
import com.example.animessenger.models.CommonModel
import com.example.animessenger.utils.*
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private val refChatList = REF_DATABASE.child(NODE_MAIN).child(UID)
    private val refUser = REF_DATABASE.child(NODE_USERS)
    private val refMessages = REF_DATABASE.child(NODE_MESSAGES).child(UID)
    private var chatItemList = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.chat)
        initRecView()
    }

    private fun initRecView() {
        recyclerView = rv_chat_items
        adapter = ChatAdapter()
        refChatList.addListenerForSingleValueEvent(AppValueEventListener {
            chatItemList = it.children.map { item ->
                item.getValue(CommonModel::class.java) ?: CommonModel()
            }
            chatItemList.forEach { model ->
                when (model.type) {
                    TYPE_CHAT -> {
                        refUser.child(model.id)
                            .addListenerForSingleValueEvent(AppValueEventListener { data ->
                                val newModel = data.getValue(CommonModel::class.java)
                                refMessages.child(model.id).limitToLast(1)
                                    .addListenerForSingleValueEvent(AppValueEventListener { items ->
                                        val tempList = items.children.map { its ->
                                            its.getValue(CommonModel::class.java)
                                        }
                                        if (tempList.isEmpty()) {
                                            newModel?.lastMessage = getString(R.string.chat_cleared)
                                        } else {
                                            newModel?.lastMessage = tempList[0]?.text.toString()
                                        }
                                        if (newModel?.name?.isEmpty() == true) {
                                            newModel.name = newModel.phone
                                        }
                                        newModel?.type = TYPE_CHAT
                                        newModel?.let { it1 -> adapter.updateList(it1) }
                                    })
                            })
                    }
                    TYPE_GROUP->{
//                        REF_DATABASE.child(NODE_GROUP).child(model.id)
//                            .addListenerForSingleValueEvent(AppValueEventListener { data ->
//                                val newModel = data.getValue(CommonModel::class.java)
//                                REF_DATABASE.child(NODE_GROUP).child(model.id).child(NODE_MESSAGES)
//                                    .limitToLast(1)
//                                    .addListenerForSingleValueEvent(AppValueEventListener { items ->
//                                        val tempList = items.children.map { its ->
//                                            its.getValue(CommonModel::class.java)
//                                        }
//                                        if (tempList.isEmpty()) {
//                                            newModel?.lastMessage = getString(R.string.chat_cleared)
//                                        } else {
//                                            newModel?.lastMessage = tempList[0]?.text.toString()
//                                        }
//                                        newModel?.type = TYPE_GROUP
//                                        newModel?.let { it1 -> adapter.updateList(it1) }
//                                    })
//                            })
                    }
                }

            }
        })
        recyclerView.adapter = adapter
    }

}