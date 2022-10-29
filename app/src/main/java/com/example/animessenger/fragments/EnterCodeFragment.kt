package com.example.animessenger.fragments

import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.animessenger.MainActivity
import com.example.animessenger.R
import com.example.animessenger.RegisterActivity
import com.example.animessenger.utils.*
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_code.*

class EnterCodeFragment(val phoneNumber: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {

    override fun onStart() {
        super.onStart()
        (activity as RegisterActivity).title = phoneNumber
        et_code.addTextChangedListener(AppTextWatcher {
            val inputCode = et_code.text.toString()
            if (inputCode.length >= 5) {
                enterCode()
            }
        })
    }

    private fun enterCode() {
        val code = et_code.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        auth.signInWithCredential(credential).addOnCompleteListener { t ->
            if (t.isSuccessful) {
                val uid = auth.currentUser?.uid.toString()
                val dataMap = mutableMapOf<String, Any>()
                dataMap[CHILD_ID] = uid
                dataMap[CHILD_PHONE] = phoneNumber
                dataMap[CHILD_USERNAME] = uid
                REF_DATABASE.child(NODE_PHONES).child(phoneNumber)
                    .setValue(uid)
                    .addOnFailureListener { showToast(it.message.toString()) }
                    .addOnSuccessListener {
                        REF_DATABASE.child(NODE_USERS).child(uid).updateChildren(dataMap)
                            .addOnSuccessListener { task ->
                                showToast(getString(R.string.welcome))
                                val intent = Intent(
                                    (activity as RegisterActivity),
                                    MainActivity::class.java
                                )
                                startActivity(intent)
                                (activity as RegisterActivity).finish()
                            }
//                                    .addOnFailureListener { showToast(it.message.toString()) }
                    }
            }
//                    else showToast(it.exception?.message.toString())
        }
    }
}

