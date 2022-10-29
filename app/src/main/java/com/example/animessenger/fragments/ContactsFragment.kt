package com.example.animessenger.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.animessenger.R
import com.example.animessenger.models.CommonModel
import com.example.animessenger.utils.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.contacts_item.view.*
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<CommonModel, ContactHolder>
    private lateinit var refContacts: DatabaseReference
    private lateinit var refUsers: DatabaseReference
    private lateinit var refAppContactsListener: AppValueEventListener // чтобы не было утечки памяти
    private var mapListener = hashMapOf<DatabaseReference, AppValueEventListener>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.contacts)
        initRecycleView()
    }

    private fun initRecycleView() {
        recyclerView = rv_contacts
        refContacts = REF_DATABASE.child(NODE_PHONES_CONTACTS).child(UID)
        val option = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(refContacts, CommonModel::class.java)
            .build()
        adapter = object : FirebaseRecyclerAdapter<CommonModel, ContactHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contacts_item, parent, false)
                return ContactHolder(view)
            }

            override fun onBindViewHolder(
                holder: ContactHolder,
                position: Int,
                model: CommonModel
            ) {
                refUsers = REF_DATABASE.child(NODE_USERS).child(model.id)
                refAppContactsListener = AppValueEventListener {
                    val contact = it.getValue(CommonModel::class.java)
                    if (contact?.name?.isEmpty() == true) {
                        holder.name.text = model.name
                    } else holder.name.text = contact?.name
                    holder.status.text = contact?.status
                    Picasso.get()
                        .load(contact?.photoUrl)
                        .placeholder(R.drawable.sakura)
                        .into(holder.photo)
                    holder.itemView.setOnClickListener {
                        parentFragmentManager.beginTransaction().replace(
                            R.id.data_container,
                            SingleChatFragment(model)
                        ).addToBackStack(null).commit()
                    }
                }
                refUsers.addValueEventListener(refAppContactsListener)
                mapListener[refUsers] = refAppContactsListener
            }
        }
        recyclerView.adapter = adapter
        adapter.startListening()
    }

    class ContactHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.tv_username_contacts
        val status: TextView = view.tv_status_contacts
        val photo: CircleImageView = view.profile_image_contacts_list
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
        mapListener.forEach {
            it.key.removeEventListener(it.value)
        }
    }
}


