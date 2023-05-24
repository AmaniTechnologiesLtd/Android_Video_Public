package com.example.amani_sdk

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity?.replaceFragmentWithBackStack(fragmentContainer: Int, fragment: Fragment) {
    this ?: return
    this.supportFragmentManager
        .beginTransaction()
        .addToBackStack(fragment.javaClass.name)
        .replace(fragmentContainer, fragment, fragment.javaClass.name)
        .commit()
}

fun AppCompatActivity.popBackStack() {
    this.supportFragmentManager.popBackStack()
}