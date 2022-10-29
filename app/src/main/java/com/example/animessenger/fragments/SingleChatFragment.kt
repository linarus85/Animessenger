package com.example.animessenger.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.*
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.animessenger.R
import com.example.animessenger.RegisterActivity
import com.example.animessenger.adapter.SingleChatAdapter
import com.example.animessenger.models.CommonModel
import com.example.animessenger.models.User
import com.example.animessenger.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.choice_upload.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleChatFragment(private val contact: CommonModel?) :
    Fragment(R.layout.fragment_single_chat) {


    private lateinit var tollbarListener: AppValueEventListener
    private lateinit var recivingUser: User
    private lateinit var refUser: DatabaseReference
    private lateinit var refMessage: DatabaseReference
    private lateinit var adapter: SingleChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messagesListener: ChildEventListener
    private var countMessagesLoad = 10
    private var isScrolling = false
    private var isScrollingSmoothToPosition = true
    private lateinit var voicceRecoder: VoiceRecorder
//    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>


    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initToolbar()
        initRecView()
        initAttachOrSend()
        initVoiceMessage()
        voicceRecoder = VoiceRecorder()
//        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_choice)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun initVoiceMessage() {
        CoroutineScope(Dispatchers.IO).launch {
            image_voice.setOnTouchListener { v, event ->
                if (checkPermission(RECORD_AUDIO)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        et_type_message.setText(getString(R.string.recording))
                        et_type_message.setBackgroundResource(R.drawable.text_view_corner)
                        APP_ACTIVITY.vibratePhone()
                        val messageKey = REF_DATABASE.child(NODE_MESSAGES).child(UID)
                            .child(contact?.id.toString()).push().key.toString()
                        voicceRecoder.startRecorder(messageKey)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        et_type_message.setText("")
                        et_type_message.setBackgroundResource(R.drawable.edit_text_corner)
                        voicceRecoder.stopRecorder { files, messageKey ->
                            uploadFileToStorage(
                                Uri.fromFile(files),
                                messageKey,
                                contact?.id,
                                TYPE_VOICE
                            )
                        }
                        isScrollingSmoothToPosition = true
                    }
                }
                true
            }
        }
    }

    private fun uploadFileToStorage(
        uri: Uri,
        messageKey: String,
        id: String?,
        type: String
    ) {
        val path = REF_STORAGE.child(STORAGE_FILES).child(messageKey)
        putFileToStorage(uri, path) {
            getUrlFromStorage(path) {
                sendFileMessage(id, it, messageKey, type)
            }
        }
    }

    private fun initAttachOrSend() {
        et_type_message.addTextChangedListener(AppTextWatcher {
            val text = et_type_message.text.toString()
            if (text.isEmpty() || text == getString(R.string.recording)) {
                send.visibility = View.GONE
                attach_file.visibility = View.VISIBLE
                image_voice.visibility = View.VISIBLE
            } else {
                send.visibility = View.VISIBLE
                attach_file.visibility = View.GONE
                image_voice.visibility = View.GONE
            }
        })
        attach_file.setOnClickListener { addImage() }
    }

//    private fun attach() {
////        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//        file_attach.setOnClickListener { attachFile() }
//        image_attach.setOnClickListener { addImage() }
//    }
//
//    private fun attachFile() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "*/*"
//        startActivityForResult(intent, FILE_REQUEST_CODE)
//    }

    private fun addImage() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(300, 300)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data).uri
                    val messageKey = REF_DATABASE.child(NODE_MESSAGES).child(UID)
                        .child(contact?.id.toString()).push().key.toString()
                    uploadFileToStorage(uri, messageKey, contact?.id, TYPE_IMAGE)
                    isScrollingSmoothToPosition = true
                }
                FILE_REQUEST_CODE -> {
//                    val uri = data.data
//                    val filename = getFilenameFromUri(uri!!)
//                    val messageKey = REF_DATABASE.child(NODE_MESSAGES).child(UID)
//                        .child(contact?.id.toString()).push().key.toString()
//                    uploadFileToStorage(uri, messageKey, contact?.id, TYPE_FILE)
//                    isScrollingSmoothToPosition = true
                }
            }
        }

    }

//    private fun getFilenameFromUri(uri: Uri): String {
//val result = ""
//        val cursor= APP_ACTIVITY.contentResolver.query(
//            uri,
//            null,
//            null,
//            null,
//        )
//    }

    inline fun putFileToStorage(
        uri: Uri,
        path: StorageReference,
        crossinline function: () -> Unit
    ) {
        /* Функция высшего порядка, отправляет картинку в хранилище */
        path.putFile(uri)
            .addOnSuccessListener { function() }
            .addOnFailureListener { showToast(it.message.toString()) }
    }

    inline fun getUrlFromStorage(
        path: StorageReference,
        crossinline function: (url: String) -> Unit
    ) {
        /* Функция высшего порядка, получает  URL картинки из хранилища */
        path.downloadUrl
            .addOnSuccessListener { function(it.toString()) }
            .addOnFailureListener { showToast(it.message.toString()) }
    }

    private fun sendFileMessage(
        id: String?,
        fileUrl: String,
        messageKey: String,
        type: String
    ) {
        val refDialogSend = "$NODE_MESSAGES/$UID/$id"
        val refDialogResive = "$NODE_MESSAGES/$id/$UID"
        val mapMessage = hashMapOf<String, Any>()
        mapMessage[CHILD_FROM_USER] = UID
        mapMessage[CHILD_TYPE] = type
        mapMessage[CHILD_ID] = messageKey
        mapMessage[CHILD_TIME] = ServerValue.TIMESTAMP
        mapMessage[CHILD_FILE_URI] = fileUrl
        val mapDialog = hashMapOf<String, Any>()
        mapDialog["$refDialogSend/$messageKey"] = mapMessage
        mapDialog["$refDialogResive/$messageKey"] = mapMessage
        REF_DATABASE.updateChildren(mapDialog)
            .addOnFailureListener { showToast(it.message.toString()) }
    }


    private fun initRecView() {
        recyclerView = rv_chat
        adapter = SingleChatAdapter()
        refMessage = REF_DATABASE.child(NODE_MESSAGES).child(UID)
            .child(contact?.id.toString())
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        //    recyclerView.isNestedScrollingEnabled = false
        messagesListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                adapter.addItem(
                    snapshot.getValue(CommonModel::class.java) ?: CommonModel(),
                    isScrollingSmoothToPosition
                )
                if (isScrollingSmoothToPosition) {
                    recyclerView.smoothScrollToPosition(adapter.itemCount)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        }

        refMessage.limitToLast(countMessagesLoad).addChildEventListener(messagesListener)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isScrolling && dy < 0) {
                    updateLoadDate()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        })
    }

    private fun updateLoadDate() {
        isScrollingSmoothToPosition = false
        isScrolling = false
        countMessagesLoad += 10
        refMessage.removeEventListener(messagesListener)
        refMessage.limitToLast(countMessagesLoad).addChildEventListener(messagesListener)
    }

    private fun initToolbar() {
        APP_ACTIVITY.toolbar.toolbar_info.visibility = View.VISIBLE
        tollbarListener = AppValueEventListener {
            recivingUser = it.getValue(User::class.java) ?: User()
            initInfoToolbar()
        }
        refUser = REF_DATABASE.child(NODE_USERS).child(contact?.id ?: "")
        refUser.addValueEventListener(tollbarListener)
        send.setOnClickListener {
            isScrollingSmoothToPosition = true
            val message = et_type_message.text.toString()
            if (message.isEmpty()) {
                showToast(getString(R.string.write_message))
            } else sendMessage(message, contact?.id, TYPE_TEXT) {
                contact?.id?.let { it1 -> saveMainList(it1, TYPE_CHAT) }
                et_type_message.setText("")
                APP_ACTIVITY.hideKeyBoardSmartphone()
            }
        }
    }

    private fun saveMainList(id: String, type: String) {
        val refUser = "$NODE_MAIN/$UID/$id"
        val refRecevied = "$NODE_MAIN/$id/$UID"
        val mapUser = hashMapOf<String, Any>()
        val mapReceived = hashMapOf<String, Any>()
        mapUser[CHILD_ID] = id
        mapUser[CHILD_TYPE] = type
        mapReceived[CHILD_ID] = UID
        mapReceived[CHILD_TYPE] = type
        val commonMap = hashMapOf<String, Any>()
        commonMap[refUser] = mapUser
        commonMap[refRecevied] = mapReceived
        REF_DATABASE.updateChildren(commonMap)
            .addOnFailureListener { showToast(it.message.toString()) }
    }

    private fun sendMessage(message: String, id: String?, typeText: String, function: () -> Unit) {
        val refDialogSend = "$NODE_MESSAGES/$UID/$id"
        val refDialogResive = "$NODE_MESSAGES/$id/$UID"
        val messageKey = REF_DATABASE.child(refDialogSend).push().key
        val mapMessage = hashMapOf<String, Any>()
        mapMessage[CHILD_FROM_USER] = UID
        mapMessage[CHILD_TYPE] = typeText
        mapMessage[CHILD_TEXT] = message
        mapMessage[CHILD_ID] = messageKey.toString()
        mapMessage[CHILD_TIME] = ServerValue.TIMESTAMP
        val mapDialog = hashMapOf<String, Any>()
        mapDialog["$refDialogSend/$messageKey"] = mapMessage
        mapDialog["$refDialogResive/$messageKey"] = mapMessage
        REF_DATABASE.updateChildren(mapDialog)
            .addOnSuccessListener { function() }
            .addOnFailureListener { showToast(it.message.toString()) }
    }

    private fun initInfoToolbar() {
        if (recivingUser.name.isEmpty()) {
            APP_ACTIVITY.toolbar.toolbar_info.tv_username_toolbar.text = contact?.name
        } else APP_ACTIVITY.toolbar.toolbar_info.tv_username_toolbar.text = recivingUser.name
        Picasso.get()
            .load(recivingUser.photoUrl)
            .placeholder(R.drawable.sakura)
            .into(APP_ACTIVITY.toolbar.toolbar_info.profile_image_toolbar)
        APP_ACTIVITY.toolbar.toolbar_info.tv_status_toolbar.text = recivingUser.status
    }

    override fun onPause() {
        super.onPause()
        APP_ACTIVITY.toolbar.toolbar_info.visibility = View.GONE
        refUser.removeEventListener(tollbarListener)
        refMessage.removeEventListener(messagesListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        voicceRecoder.releaseRecorder()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.chat_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear_chat -> clearChat(contact?.id) {
                showToast(getString(R.string.chat_cleared))
                parentFragmentManager.beginTransaction().replace(
                    R.id.data_container,
                    ChatFragment()
                ).addToBackStack(null).commit()
            }
            R.id.menu_delete_chat -> deleteChat(contact?.id) {
                showToast(getString(R.string.chat_deleted))
                parentFragmentManager.beginTransaction().replace(
                    R.id.data_container,
                    ChatFragment()
                ).addToBackStack(null).commit()
            }
        }
        return true
    }

    private fun deleteChat(id: String?, function: () -> Unit) {
        id?.let {
            REF_DATABASE.child(NODE_MAIN).child(UID).child(it)
                .removeValue()
                .addOnFailureListener { showToast(it.message.toString()) }
                .addOnSuccessListener {function()}
        }
    }

    private fun clearChat(id: String?, function: () -> Unit) {
        id?.let { let ->
            REF_DATABASE.child(NODE_MESSAGES).child(UID).child(let)
                .removeValue()
                .addOnFailureListener { showToast(it.message.toString()) }
                .addOnSuccessListener {
                    REF_DATABASE.child(NODE_MESSAGES).child(id)
                        .child(UID).removeValue()
                        .addOnSuccessListener { function() }
                }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
    }

}

//image_voice.setColorFilter(ContextCompat.getColor(APP_ACTIVITY,R.color.colorPrimary))
//image_voice.colorFilter = null
