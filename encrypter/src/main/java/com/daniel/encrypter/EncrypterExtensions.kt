package com.daniel.encrypter

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

fun showLogD(text: String) {
    Log.d("APP_TAG", text)
}

fun showLogD(tag: String, msg: String) {
    Log.d(tag, msg)
}

fun showLogE(text: String) {
    Log.e("APP_TAG", text)
}

fun showLogE(customTag: String, text: String) {
    Log.e(customTag, text)
}


fun logError(tag: String, text: String) {
    Log.e(tag, text)
}

@SuppressLint("SimpleDateFormat")
fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
    return format.format(date)
}

fun Long.elapsedTime(): String {
    return if (this < 1000L) {
        "$this milli-seconds"
    } else {
        val elapsedTime = this / 1000
        if (elapsedTime > 60) {
            "${elapsedTime / 60} minutes ${(elapsedTime % 60)} seconds"
        } else "$elapsedTime seconds"
    }
}

fun printLine(tag: String) {
    Log.e(
        tag,
        "\n\n______________________________________________________________________________________________________ \n\n"
    )
}

fun Long.toMB() =
    (((this.toDouble() / (1024 * 1024)) * 10).roundToInt() / 10).toFloat()

fun currentTimeToLong(): Long {
    return System.currentTimeMillis()
}

@SuppressLint("SimpleDateFormat")
fun convertDateToLong(date: String): Long {
    val df = SimpleDateFormat("yyyy.MM.dd HH:mm")
    return df.parse(date).time
}
