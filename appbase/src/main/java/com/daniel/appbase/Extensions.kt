package com.daniel.appbase

import android.content.Context
import android.util.Log
import android.widget.Toast

fun showLogD(text: String) {
    Log.d("APP_TAG", text)
}

fun showLogD(text: String, customTag: String) {
    Log.d(customTag, text)
}

fun showLogE(text: String) {
    Log.e("APP_TAG", text)
}

fun showLogE(text: String, customTag: String) {
    Log.e(customTag, text)
}

fun Context.toast( text: String) =
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()


fun showLogToast(context: Context, text: String) =
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
