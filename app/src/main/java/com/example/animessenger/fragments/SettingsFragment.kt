package com.example.animessenger.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.example.animessenger.MainActivity
import com.example.animessenger.R
import com.example.animessenger.RegisterActivity
import com.example.animessenger.utils.*
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {
    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
    }

    private fun initFields() {
        tv_info.text = USER.bio
        tv_name.text = USER.name
        tv_phone_number.text = USER.phone
        tv_status_header_settings.text = USER.status
        tv_username_header_settings.text = USER.name
        tv_name.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(
                R.id.data_container,
                ChangeNameFragment()
            ).addToBackStack(null).commit()
        }
        tv_info.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(
                R.id.data_container,
                ChangeBioFragment()
            ).addToBackStack(null).commit()
        }
        btn_image_photo.setOnClickListener { changeUserPhoto() }
        Picasso.get()
            .load(USER.photoUrl)
            .placeholder(R.drawable.sakura)
            .into(profile_image)
    }

    private fun changeUserPhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(300, 300)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null
        ) {
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE.child(STORAGE_PROFILE_IMAGE).child(UID)
            path.putFile(uri).addOnCompleteListener {
                if (it.isSuccessful) {
                    path.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val photoUrl = task.result.toString()
                            REF_DATABASE.child(NODE_USERS).child(UID)
                                .child(CHILD_PHOTO_URL).setValue(photoUrl)
                                .addOnCompleteListener { task1 ->
                                    if (task1.isSuccessful) {
                                        Picasso.get()
                                            .load(photoUrl)
                                            .placeholder(R.drawable.sakura)
                                            .into(profile_image)
                                        showToast(getString(R.string.data_updated))
                                        USER.photoUrl = photoUrl
                                        APP_ACTIVITY.appDriver.updateHeader()
                                    }
                                }
                        }
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                AppUserStatus.updateStatus(AppUserStatus.OFFLINE)
                auth.signOut()
                val intent = Intent(
                    (APP_ACTIVITY),
                    RegisterActivity::class.java
                )
                startActivity(intent)
                (APP_ACTIVITY).finish()
            }
            R.id.edit_name -> {
                parentFragmentManager.beginTransaction().replace(
                    R.id.data_container,
                    ChangeNameFragment()
                ).addToBackStack(null).commit()
            }
        }
        return true
    }
}