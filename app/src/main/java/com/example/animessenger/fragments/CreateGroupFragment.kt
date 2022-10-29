package com.example.animessenger.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.animessenger.R
import com.example.animessenger.adapter.GroupAdapter
import com.example.animessenger.models.CommonModel
import com.example.animessenger.utils.*
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.create_group.*

class CreateGroupFragment(var listContacts: List<CommonModel>) : Fragment(R.layout.create_group) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroupAdapter
    private var uri = Uri.EMPTY

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.create_group)
        initRecycleView()
        profile_image_group.setOnClickListener { addPhoto() }
        fl_btn_create_group_complete.setOnClickListener {
            val nameGroup = et_group_name.text.toString()
            if (nameGroup.isEmpty()) {
                showToast(getString(R.string.group_name))
            } else {
                createGroupToDb(nameGroup, uri, listContacts) {
                    parentFragmentManager.beginTransaction().replace(
                        R.id.data_container,
                        ChatFragment()
                    ).addToBackStack(null).commit()
                }
                APP_ACTIVITY.hideKeyBoardSmartphone()
            }
        }
        et_group_name.requestFocus()
        tv_text_account.text = "${getString(R.string.members)}: ${listContacts.size}"
    }

    private fun createGroupToDb(
        nameGroup: String,
        uri: Uri,
        listContacts: List<CommonModel>,
        function: () -> Int
    ) {
        val keyGroup = REF_DATABASE.child(NODE_GROUP).push().key.toString()
        val path = REF_DATABASE.child(NODE_GROUP).child(keyGroup)
        val pathStorage = REF_STORAGE.child(CHILD_GROUP_IMAGE).child(keyGroup)
        val mapData = hashMapOf<String, Any>()
        mapData[CHILD_ID] = keyGroup
        mapData[CHILD_NAME] = nameGroup
        mapData[CHILD_PHOTO_URL] = "empty"
        val mapMembers = hashMapOf<String, Any>()
        listContacts.forEach {
            mapMembers[it.id] = USER_MEMBER
        }
        mapMembers[UID] = USER_CREATOR
        mapData[NODE_MEMBERS] = mapMembers
        path.updateChildren(mapData)
            .addOnSuccessListener {
                if (uri != Uri.EMPTY) {
                    putFileToStorage(uri, pathStorage) {
                        getUrlFromStorage(pathStorage) {
                            path.child(CHILD_PHOTO_URL).setValue(it)
                            addGroupToMainList(mapData, listContacts) {
                                function()
                            }
                        }
                    }
                } else {
                    addGroupToMainList(mapData, listContacts) {
                        function()
                    }
                }
            }
            .addOnFailureListener { showToast(it.message.toString()) }
    }

    private fun addGroupToMainList(
        mapData: HashMap<String, Any>,
        listContacts: List<CommonModel>,
        function: () -> Unit
    ) {
        val path = REF_DATABASE.child(NODE_MAIN)
        val map = hashMapOf<String, Any>()
        map[CHILD_ID] = mapData[CHILD_ID].toString()
        map[CHILD_TYPE] = TYPE_GROUP
        listContacts.forEach {
            path.child(it.id).child(map[CHILD_ID].toString())
                .updateChildren(map)
        }
        path.child(UID).child(map[CHILD_ID].toString())
            .updateChildren(map)
            .addOnSuccessListener { function() }
            .addOnFailureListener { showToast(it.message.toString()) }
    }


    private fun addPhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(300, 300)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            uri = CropImage.getActivityResult(data).uri
            profile_image_group.setImageURI(uri)
        }
    }


    private fun initRecycleView() {
        recyclerView = rv_group_items_create
        adapter = GroupAdapter()
        recyclerView.adapter = adapter
        listContacts.forEach { adapter.updateList(it) }
    }

    private inline fun putFileToStorage(
        uri: Uri,
        path: StorageReference,
        crossinline function: () -> Unit
    ) {
        /* Функция высшего порядка, отправляет картинку в хранилище */
        path.putFile(uri)
            .addOnSuccessListener { function() }
            .addOnFailureListener { showToast(it.message.toString()) }
    }

    private inline fun getUrlFromStorage(
        path: StorageReference,
        crossinline function: (url: String) -> Unit
    ) {
        /* Функция высшего порядка, получает  URL картинки из хранилища */
        path.downloadUrl
            .addOnSuccessListener { function(it.toString()) }
            .addOnFailureListener { showToast(it.message.toString()) }
    }

}