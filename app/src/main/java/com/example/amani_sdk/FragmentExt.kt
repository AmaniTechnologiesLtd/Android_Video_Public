package com.example.amani_sdk

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.amani_sdk.FragmentExt.runOnUiThread

object FragmentExt {

    fun AppCompatActivity?.replaceFragmentWithBackStack(fragmentContainer: Int, fragment: Fragment) {
        this ?: return
        this.supportFragmentManager
            .beginTransaction()
            .addToBackStack(fragment.javaClass.name)
            .replace(fragmentContainer, fragment, fragment.javaClass.name)
            .commit()
    }

    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }
}