package com.example.animessenger.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.animessenger.MainActivity

open class BaseFragment( layout:Int): Fragment(layout) {

    override fun onStart() {
        super.onStart()
//        (activity as MainActivity).appDriver.disableDrawer()
    }

    override fun onStop() {
        super.onStop()
//        (activity as MainActivity).appDriver.enableDrawer()
    }

}