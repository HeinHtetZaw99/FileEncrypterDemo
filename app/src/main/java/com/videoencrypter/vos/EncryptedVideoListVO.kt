package com.videoencrypter.vos

data class EncryptedVideoListVO(
    var videoList: List<VideoPreviewVO>?
) : BaseVideoListVO