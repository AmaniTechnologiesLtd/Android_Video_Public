package com.example.amani_sdk

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.amani.ai.R
import com.google.android.material.snackbar.Snackbar

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

fun AppCompatActivity.removeFragment(fragment: Fragment?) {
    if (fragment == null) return
    this.supportFragmentManager.beginTransaction().remove(fragment).commit()
}

fun AppCompatActivity.alertDialog(
    title: String,
    message: String,
    positiveButton: String,
    negativeButton: String,
    positiveClick: () -> Unit,
    negativeClick: () -> Unit
){
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(positiveButton) { _, _ ->
        positiveClick()
    }
    builder.setNegativeButton(negativeButton) { _, _ ->
        negativeClick()
    }
    val dialog = builder.create()
    dialog.show()
}
fun AppCompatActivity.snackBar(message: String) {
    Snackbar.make(
        findViewById(R.id.layout),
        message,
        Snackbar.LENGTH_SHORT
    ).show()
}