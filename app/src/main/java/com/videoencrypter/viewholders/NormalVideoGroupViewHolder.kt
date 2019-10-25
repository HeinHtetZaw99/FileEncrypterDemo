package com.videoencrypter.viewholders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.daniel.appbase.recyclerview.BaseViewHolder
import com.videoencrypter.adapters.VideoAdapter
import com.videoencrypter.delegates.VideoDelegate
import com.videoencrypter.vos.VideoListVO
import kotlinx.android.synthetic.main.cardview_group.view.*

class NormalVideoGroupViewHolder(
   itemView: View,
    private val delegate: VideoDelegate
) : BaseViewHolder<VideoListVO>(itemView) {
    override fun setData(mData: VideoListVO) {
        val adapter = VideoAdapter(itemView.context , delegate)
        itemView.itemsRv.layoutManager = LinearLayoutManager(itemView.context)
        itemView.itemsRv.adapter = adapter
        adapter.appendNewData(mData.videoList!!)
    }

}