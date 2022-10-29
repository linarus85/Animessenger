package com.example.animessenger

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.provider.ContactsContract
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.animessenger.databinding.ActivityMainBinding
import com.example.animessenger.fragments.ChatFragment
import com.example.animessenger.models.CommonModel
import com.example.animessenger.models.User
import com.example.animessenger.ui.AppDriver
import com.example.animessenger.utils.*
import kotlinx.android.synthetic.main.contacts_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var appDriver: AppDriver
    lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUserFromDb {
            CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }
            initFields()
            initFun()
        }
    }


    private fun initContacts() {
        if (checkPermission(READ_CONTACTS)) {
            val array = arrayListOf<CommonModel>()
            val cursor = APP_ACTIVITY.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null,
            )
            cursor?.let {
                while (cursor.moveToNext()) {
                    val name = it.getString(
                        it.getColumnIndexOrThrow(
                            ContactsContract.Contacts.DISPLAY_NAME
                        )
                    )
                    val phone = it.getString(
                        it.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        )
                    )
                    val newModel = CommonModel()
                    newModel.name = name
                    newModel.phone = phone.replace(Regex("[\\s,-]"), "")
                    array.add(newModel)
                }
            }
            cursor?.close()
            updatePhonesToDatabase(array)
        }
    }

    private fun updatePhonesToDatabase(array: ArrayList<CommonModel>) {
        if (auth.currentUser != null) {
            REF_DATABASE.child(NODE_PHONES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    it.children.forEach { snap ->
                        array.forEach { contact ->
//                            if (snap.key == contact.phone) {
                                REF_DATABASE.child(NODE_PHONES_CONTACTS).child(UID)
                                    .child(snap.value.toString()).child(CHILD_ID)
                                    .setValue(snap.value.toString())
                                    .addOnFailureListener {
                                        showToast(it.message.toString())
                                    }
                            REF_DATABASE.child(NODE_PHONES_CONTACTS).child(UID)
                                .child(snap.value.toString()).child(CHILD_NAME)
                                .setValue(contact.name)
                                .addOnFailureListener {
                                    showToast(it.message.toString())
                                }
//                            }
                        }
                    }
                })
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            initContacts()
        }
    }

    override fun onStart() {
        super.onStart()
        AppUserStatus.updateStatus(AppUserStatus.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppUserStatus.updateStatus(AppUserStatus.OFFLINE)
    }


    private fun initFun() {
        if (auth.currentUser != null) {
            setSupportActionBar(toolbar)
            supportFragmentManager.beginTransaction().replace(
                R.id.data_container,
                ChatFragment()
            ).commit()
            appDriver.createDriver()
        } else {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun initFields() {
        toolbar = binding.toolbar
        appDriver = AppDriver(this, toolbar)
    }

    inline fun initUserFromDb(crossinline function: () -> Unit) {
        REF_DATABASE.child(NODE_USERS).child(UID)
            .addListenerForSingleValueEvent(AppValueEventListener {
                USER = it.getValue(User::class.java) ?: User()
//                if (USER.name.isEmpty()) {
//                    USER.name = UID
//                }
                function()
            })
    }

    fun hideKeyBoardSmartphone() {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }
    fun vibratePhone() {
        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE) )
        }else{
            @Suppress("DEPRECATION")
            vib.vibrate(50)
        }
    }
}