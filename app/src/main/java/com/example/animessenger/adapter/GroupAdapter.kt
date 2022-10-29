package com.example.animessenger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.animessenger.R
import com.example.animessenger.fragments.GroupFragment
import com.example.animessenger.fragments.SingleChatFragment
import com.example.animessenger.models.CommonModel
import com.example.animessenger.utils.APP_ACTIVITY
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_group.view.*
import kotlinx.android.synthetic.main.chat_item.view.*

class GroupAdapter : RecyclerView.Adapter<GroupAdapter.Holder>() {
    private val chatList = mutableListOf<CommonModel>()

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val groupName: TextView = view.tv_name_contacts
        val groupMessage: TextView = view.tv_contacts_message
        val groupPhoto: CircleImageView = view.profile_image_add_contacts
        val checkContact: CircleImageView = view.profile_image_item_choice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_group, parent, false)
        val holder = Holder(view)
        holder.itemView.setOnClickListener {
            if (chatList[holder.absoluteAdapterPosition].isChoice) {
                holder.checkContact.visibility = View.INVISIBLE
                chatList[holder.absoluteAdapterPosition].isChoice = false
                GroupFragment.listContacts.remove(chatList[holder.absoluteAdapterPosition])
            } else {
                holder.checkContact.visibility = View.VISIBLE
                chatList[holder.absoluteAdapterPosition].isChoice = true
                GroupFragment.listContacts.add(chatList[holder.absoluteAdapterPosition])
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.groupName.text = chatList[position].name
        holder.groupMessage.text = chatList[position].lastMessage
        Picasso.get()
            .load(chatList[position].photoUrl)
            .fit()
            .placeholder(R.drawable.sakura)
            .into(holder.groupPhoto)
    }

    override fun getItemCount(): Int = chatList.size

    fun updateList(item: CommonModel) {
        chatList.add(item)
        notifyItemInserted(chatList.size)
    }
}