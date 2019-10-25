package com.daniel.encrypter

interface FileListEncryptionListener {
    fun onFinishedEncryptingFilesList()
    fun onFinishedDecryptingFilesList()
    fun onStartEncryptingFilesList()
    fun onStartDecryptingFilesList()
    fun onCancelled(msg : String)
    fun onProgressUpdate(percentage: Int)
}