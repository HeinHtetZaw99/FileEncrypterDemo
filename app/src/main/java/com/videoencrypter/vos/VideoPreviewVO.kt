package com.videoencrypter.vos

import android.net.Uri

data class VideoPreviewVO(
    var videoName: String? = "",
    var dateCreatedAt: String? = "",
    var uri: Uri? = null,
    var isEncrypted: Boolean,
    var size: String?
)