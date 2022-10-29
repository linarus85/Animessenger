package com.example.animessenger.fragments

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.animessenger.R
import com.example.animessenger.adapter.ChatAdapter
import com.example.animessenger.adapter.GroupAdapter
import com.example.animessenger.models.CommonModel
import com.example.animessenger.utils.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_group.*

class GroupFragment : Fragment(R.layout.fragment_group) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroupAdapter
    private val refChatList = REF_DATABASE.child(NODE_PHONES_CONTACTS).child(UID)
    private val refUser = REF_DATABASE.child(NODE_USERS)
    private val refMessages = REF_DATABASE.child(NODE_MESSAGES).child(UID)
    private var chatItemList = listOf<CommonModel>()

    override fun onResume() {
        listContacts.clear()
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.add_member)
        APP_ACTIVITY.hideKeyBoardSmartphone()
        initRecView()
        fl_btn_add_contacts_next.setOnClickListener {
            if (listContacts.isEmpty()) showToast(getString(R.string.add_member))
            else {
                parentFragmentManager.beginTransaction().replace(
                    R.id.data_container,
                    CreateGroupFragment(listContacts)
                ).addToBackStack(null).commit()
            }
        }
    }

    private fun initRecView() {
        recyclerView = rv_group_items
        adapter = GroupAdapter()
        refChatList.addListenerForSingleValueEvent(AppValueEventListener {
            chatItemList = it.children.map { item ->
                item.getValue(CommonModel::class.java) ?: CommonModel()
            }
            chatItemList.forEach { model ->
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
                                newModel?.let { it1 -> adapter.updateList(it1) }
                            })
                    })
            }
        })
        recyclerView.adapter = adapter
    }

    companion object {
        val listContacts = mutableListOf<CommonModel>()
    }
}