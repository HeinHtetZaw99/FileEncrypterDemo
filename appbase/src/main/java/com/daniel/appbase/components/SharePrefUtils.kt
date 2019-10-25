package com.daniel.appbase.components


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.daniel.appbase.showLogD
import java.util.*


/**
 * created by Daniel McCoy @ 28th, May , 2019
 */
class SharePrefUtils private constructor(private val context: Context) {
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun clearALL() {
        val keyList = ArrayList(EnumSet.allOf(KEYS::class.java))
        showLogD(" -$keyList <-")
        for (key in keyList) {
                sharedPreferences.edit().putString(context.getString(key.label), key.defaultValue)
                    .apply()
        }
    }

    fun delete(key: KEYS) {
        sharedPreferences.edit().putString(context.getString(key.label), key.defaultValue).apply()

    }

    fun save(key: KEYS, value: String) {
        sharedPreferences.edit().putString(context.getString(key.label), value).apply()
    }

    fun load(key: KEYS): String? {
        return sharedPreferences.getString(context.getString(key.label), key.defaultValue)
    }

    fun save(key: KEYI, value: Int) {
        sharedPreferences.edit().putInt(context.getString(key.label), value).apply()
    }


    fun load(key: KEYI): Int {
        return sharedPreferences.getInt(context.getString(key.label), key.defaultValue)
    }

    fun save(key: KEYB, value: Boolean) {
        sharedPreferences.edit().putBoolean(context.getString(key.label), value).apply()
    }

    fun load(key: KEYB): Boolean {
        return sharedPreferences.getBoolean(context.getString(key.label), key.defaultValue)
    }


    /**
     * Define your keys here and set Default Values here
     * In case if u had more SP_VAlUES , just modify here
     */

    enum class KEYS(val label: Int, val defaultValue: String)

    enum class KEYB(val label: Int, val defaultValue: Boolean)

    enum class KEYI(val label: Int, val defaultValue: Int)

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: SharePrefUtils? = null

        val instance: SharePrefUtils
            get() = if (INSTANCE == null)
                throw RuntimeException("SharePrefUtils has not been initialized")
            else
                INSTANCE!!

        fun init(context: Context) {
            if (INSTANCE == null)
                INSTANCE = SharePrefUtils(context)
        }
    }
}
