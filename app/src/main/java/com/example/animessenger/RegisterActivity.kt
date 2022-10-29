package com.example.animessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.animessenger.databinding.ActivityRegisterBinding
import com.example.animessenger.fragments.EnterPhoneNumberFragment
import com.example.animessenger.utils.initFirebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFirebase()
    }

    override fun onStart() {
        super.onStart()
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        title = getString(R.string.your_phone)
        supportFragmentManager.beginTransaction().add(
            R.id.data_container,
            EnterPhoneNumberFragment()
        ).commit()
    }
}