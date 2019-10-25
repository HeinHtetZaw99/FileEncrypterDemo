package com.videoencrypter.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.daniel.appbase.recyclerview.BaseRecyclerAdapter
import com.videoencrypter.R
import com.videoencrypter.databinding.CardviewVideoItemBinding
import com.videoencrypter.delegates.VideoDelegate
import com.videoencrypter.viewholders.VideoViewHolder
import com.videoencrypter.vos.VideoPreviewVO

class VideoAdapter(
    context: Context,
    private val delegate: VideoDelegate
) :
    BaseRecyclerAdapter<VideoPreviewVO, VideoViewHolder>(context) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoViewHolder {
        val binding = DataBindingUtil.inflate<CardviewVideoItemBinding>(
            mLayoutInflator,
            R.layout.cardview_video_item,
            parent,
            false
        )
        return VideoViewHolder(binding,delegate)
    }

}