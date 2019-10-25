package com.videoencrypter.viewholders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.daniel.appbase.recyclerview.BaseViewHolder
import com.videoencrypter.adapters.VideoAdapter
import com.videoencrypter.delegates.VideoDelegate
import com.videoencrypter.vos.EncryptedVideoListVO
import kotlinx.android.synthetic.main.cardview_group.view.*

class EncryptedVideoGroupViewHolder(
    itemView: View,
    private val delegate: VideoDelegate
) :
    BaseViewHolder<EncryptedVideoListVO>(itemView) {
    override fun setData(mData: EncryptedVideoListVO) {
        val adapter = VideoAdapter(itemView.context, delegate)
        itemView.itemsRv.layoutManager = LinearLayoutManager(itemView.context)
        itemView.itemsRv.adapter = adapter
        adapter.appendNewData(mData.videoList!!)
    }
}