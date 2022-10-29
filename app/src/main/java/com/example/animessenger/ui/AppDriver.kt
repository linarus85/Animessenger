package com.example.animessenger.ui

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.animessenger.R
import com.example.animessenger.adapter.GroupAdapter
import com.example.animessenger.fragments.ChatFragment
import com.example.animessenger.fragments.ContactsFragment
import com.example.animessenger.fragments.GroupFragment
import com.example.animessenger.fragments.SettingsFragment
import com.example.animessenger.utils.USER
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*

class AppDriver(var activity: AppCompatActivity, var toolbar: Toolbar) {

    private lateinit var drawer: Drawer
    private lateinit var header: AccountHeader
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var profile: ProfileDrawerItem

    fun createDriver() {
        imageLoader()
        createHeader()
        createDrawer()
//        drawerLayout = drawer.drawerLayout
    }

//    fun disableDrawer() {
//        drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
//        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//        toolbar.setNavigationOnClickListener {
//            activity.supportFragmentManager.popBackStack()
//        }
//    }

//    fun enableDrawer() {
//        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
//        drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = true
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//        drawer.openDrawer()
//    }

    private fun createDrawer() {
        drawer = DrawerBuilder()
            .withActivity(activity)
            .withToolbar(toolbar)
            .withActionBarDrawerToggle(true)
            .withSelectedItem(-1)
            .withAccountHeader(header)
            .addDrawerItems(
//                PrimaryDrawerItem().withIdentifier(1)
//                    .withIconTintingEnabled(true)
//                    .withName(R.string.create_group)
//                    .withSelectable(false)
//                    .withIcon(R.drawable.ic_groops),
                PrimaryDrawerItem().withIdentifier(1)
                    .withIconTintingEnabled(true)
                    .withName(R.string.secret_chat)
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_lock_message),
//                PrimaryDrawerItem().withIdentifier(3)
//                    .withIconTintingEnabled(true)
//                    .withName(R.string.create_channel)
//                    .withSelectable(false)
//                    .withIcon(R.drawable.ic_channel),
                PrimaryDrawerItem().withIdentifier(2)
                    .withIconTintingEnabled(true)
                    .withName(R.string.сontacts)
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_users),
//                PrimaryDrawerItem().withIdentifier(5)
//                    .withIconTintingEnabled(true)
//                    .withName(R.string.calls)
//                    .withSelectable(false)
//                    .withIcon(R.drawable.ic_phone),
//                PrimaryDrawerItem().withIdentifier(6)
//                    .withIconTintingEnabled(true)
//                    .withName(R.string.favorites)
//                    .withSelectable(false)
//                    .withIcon(R.drawable.ic_faforite),
                DividerDrawerItem(),
                PrimaryDrawerItem().withIdentifier(3)
                    .withIconTintingEnabled(true)
                    .withName(R.string.settings)
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_settings),

//                PrimaryDrawerItem().withIdentifier(8)
//                    .withIconTintingEnabled(true)
//                    .withName(R.string.invite_friends)
//                    .withSelectable(false)
//                    .withIcon(R.drawable.ic_add_contact),
//                PrimaryDrawerItem().withIdentifier(9)
//                    .withIconTintingEnabled(true)
//                    .withName(R.string.questions)
//                    .withSelectable(false)
//                    .withIcon(R.drawable.ic_question),
            ).withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    when (position) {
                        4 -> activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.data_container, SettingsFragment())
                            .addToBackStack(null)
                            .commit()
                        2 -> activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.data_container, ContactsFragment())
                            .addToBackStack(null)
                            .commit()
                        1 -> activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.data_container, ChatFragment())
                            .addToBackStack(null)
                            .commit()
//                        1 -> activity.supportFragmentManager.beginTransaction()
//                            .replace(R.id.data_container, GroupFragment())
//                            .addToBackStack(null)
//                            .commit()
                    }
                    return false
                }
            }).build()
    }

    private fun createHeader() {
        profile = ProfileDrawerItem()
            .withName(USER.name)
            .withEmail(USER.phone)
            .withIcon(USER.photoUrl)
            .withIdentifier(200)
        header = AccountHeaderBuilder()
            .withActivity(activity)
            .withHeaderBackground(R.drawable.header)
            .addProfiles(
                profile
            ).build()
    }

    fun updateHeader() {
        profile
            .withName(USER.name)
            .withEmail(USER.phone)
            .withIcon(USER.photoUrl)
        header.updateProfile(profile)
    }

    fun imageLoader() {
        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable) {
                Picasso.get()
                    .load(uri)
                    .fit()
                    .placeholder(placeholder)
                    .into(imageView)
            }
        })
    }


}