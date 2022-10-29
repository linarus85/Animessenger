package com.example.animessenger.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.animessenger.MainActivity
import com.example.animessenger.R
import com.example.animessenger.utils.*
import kotlinx.android.synthetic.main.fragment_change_bio.*


class ChangeBioFragment : Fragment(R.layout.fragment_change_bio) {


    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        et_bio_settings_in_list.setText(USER.bio)
    }
    override fun onStop() {
        super.onStop()
        APP_ACTIVITY.hideKeyBoardSmartphone()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as MainActivity).menuInflater.inflate(R.menu.chec_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.check -> changeBio()
        }
        return true
    }

    private fun changeBio() {
        val bio = et_bio_settings_in_list.text.toString()
        if (bio.isEmpty()) {
            showToast(getString(R.string.tell_about_yourself))
        } else {
            REF_DATABASE.child(NODE_USERS).child(UID).child(CHILD_BIO)
                .setValue(bio).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast(getString(R.string.data_updated))
                        USER.bio = bio
                        parentFragmentManager.popBackStack()
                    }
                }
        }
    }

}