package com.daniel.encrypter

interface BaseListener {
    fun onStartEncryption(msg: String)
    fun onFinishedEncryption(msg: String)
    fun onStartDecryption(msg: String)
    fun onFinishedDecryption(msg: String)
    fun onFinished()
    fun onCancelled(msg: String)
}