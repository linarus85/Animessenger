package com.example.animessenger.fragments

import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.animessenger.MainActivity
import com.example.animessenger.R
import com.example.animessenger.RegisterActivity
import com.example.animessenger.utils.auth
import com.example.animessenger.utils.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_phone_number.*
import java.util.concurrent.TimeUnit


class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {

    private lateinit var phoneNumber: String
    private lateinit var callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()
        callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast(getString(R.string.welcome))
                        val intent = Intent(
                            (activity as RegisterActivity),
                            MainActivity::class.java
                        )
                        startActivity(intent)
                        (activity as RegisterActivity).finish()
                    }
//                    else showToast(it.exception?.message.toString())
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                parentFragmentManager.beginTransaction().replace(
                    R.id.data_container,
                    EnterCodeFragment(phoneNumber,id)
                ).addToBackStack(null).commit()
            }

        }
        fl_btn_next.setOnClickListener {
            enterPhone()
            showToast(getString(R.string.wait_come_code_number))
        }
    }

    private fun enterPhone() {
        if (et_phone_number.text.toString().isEmpty()) {
            showToast(getString(R.string.enter_phone))
        } else {
            authUser()
        }
    }

    private fun authUser() {
        phoneNumber = et_phone_number.text.toString()
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions
                .newBuilder(FirebaseAuth.getInstance())
                .setActivity(activity as RegisterActivity)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callback)
                .build()
        )

    }
}