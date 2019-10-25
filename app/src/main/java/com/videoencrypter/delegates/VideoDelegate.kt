package com.videoencrypter.delegates

import android.net.Uri

interface VideoDelegate {
    fun encryptVideo(uri: Uri)
    fun decryptVideo(uri: Uri)
}