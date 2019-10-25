package com.videoencrypter.viewholders

import android.net.Uri
import com.bumptech.glide.Glide
import com.daniel.appbase.recyclerview.BaseViewHolder
import com.daniel.encrypter.FetchPath.getPath
import com.videoencrypter.databinding.CardviewVideoItemBinding
import com.videoencrypter.delegates.VideoDelegate
import com.videoencrypter.vos.VideoPreviewVO
import java.io.File
import java.lang.NullPointerException

class VideoViewHolder(
    private var binding: CardviewVideoItemBinding,
    private var delegate: VideoDelegate
) :
    BaseViewHolder<VideoPreviewVO>(binding.root) {
    override fun setData(mData: VideoPreviewVO) {
        binding.data = mData


        try {
            if (mData.uri != null) {
                val path = getPath(
                    binding.root.context,
                    mData.uri!!
                )
                if (path != null)
                    Glide.with(binding.root.context)
                        .load(
                            Uri.fromFile(
                                File(
                                    path
                                )
                            )
                        )
                        .into(binding.previewIv)

                binding.encryptedStatusTv.text = if (mData.isEncrypted) "Encrypted" else "Decrypted"

                binding.encryptedBtn.setOnClickListener {
                    /* if (mData.isEncrypted)
                 delegate.decryptVideo(Uri.parse(mData.uri!!))
             else
                 delegate.encryptVideo(Uri.parse(mData.uri!!))*/
                }
            }
        }catch (npe : NullPointerException){
        }
    }


}