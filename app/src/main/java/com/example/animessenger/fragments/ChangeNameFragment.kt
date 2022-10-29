package com.example.animessenger.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.animessenger.MainActivity
import com.example.animessenger.R
import com.example.animessenger.utils.*
import kotlinx.android.synthetic.main.fragment_change_name.*

class ChangeNameFragment : Fragment(R.layout.fragment_change_name) {

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        et_name_settings.setText(USER.name)
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
            R.id.check -> changeName()
        }
        return true
    }

    private fun changeName() {
        val name = et_name_settings.text.toString()
        if (name.isEmpty()) {
            showToast(getString(R.string.add_your_name))
        } else {
            REF_DATABASE.child(NODE_USERS).child(UID).child(CHILD_NAME)
                .setValue(name).addOnCompleteListener {
                    if (it.isSuccessful){
                        showToast(getString(R.string.data_updated))
                        USER.name = name
                        APP_ACTIVITY.appDriver.updateHeader()
                        parentFragmentManager.popBackStack()
                    }
                }
        }
    }
}