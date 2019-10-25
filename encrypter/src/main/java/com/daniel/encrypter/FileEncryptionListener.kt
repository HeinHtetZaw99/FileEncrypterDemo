package com.daniel.encrypter

interface FileEncryptionListener : BaseListener {
    fun onStartWritingFile(msg: String)
    fun onFailedWritingFile(msg: String)
}