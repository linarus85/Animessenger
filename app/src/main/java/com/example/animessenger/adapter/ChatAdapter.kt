package com.example.animessenger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.animessenger.R
import com.example.animessenger.fragments.GroupChatFragment
import com.example.animessenger.fragments.SingleChatFragment
import com.example.animessenger.models.CommonModel
import com.example.animessenger.utils.APP_ACTIVITY
import com.example.animessenger.utils.TYPE_CHAT
import com.example.animessenger.utils.TYPE_GROUP
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_item.view.*

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.Holder>() {
    private val chatList = mutableListOf<CommonModel>()

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val chatName: TextView = view.tv_name_chat
        val chatLastMessage: TextView = view.tv_last_message
        val chatPhoto: ImageView = view.profile_image_chat_list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        val holder = Holder(view)
        holder.itemView.setOnClickListener {
            when(chatList[holder.absoluteAdapterPosition].type){
                TYPE_CHAT->{
                    APP_ACTIVITY.supportFragmentManager.beginTransaction().replace(
                        R.id.data_container,
                        SingleChatFragment(chatList[holder.absoluteAdapterPosition])
                    ).addToBackStack(null).commit()
                }
                TYPE_GROUP->{
                    APP_ACTIVITY.supportFragmentManager.beginTransaction().replace(
                        R.id.data_container,
                        GroupChatFragment(chatList[holder.absoluteAdapterPosition])
                    ).addToBackStack(null).commit()
                }
            }

        }
        return holder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.chatName.text = chatList[position].name
        holder.chatLastMessage.text = chatList[position].lastMessage
        Picasso.get()
            .load(chatList[position].photoUrl)
            .fit()
            .placeholder(R.drawable.sakura)
            .into(holder.chatPhoto)
    }

    override fun getItemCount(): Int = chatList.size

    fun updateList(item: CommonModel) {
        chatList.add(item)
        notifyItemInserted(chatList.size)
    }
}