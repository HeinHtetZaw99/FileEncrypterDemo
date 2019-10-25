package com.videoencrypter.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.daniel.appbase.recyclerview.BaseRecyclerAdapter
import com.daniel.appbase.recyclerview.BaseViewHolder
import com.videoencrypter.R
import com.videoencrypter.delegates.VideoDelegate
import com.videoencrypter.viewholders.EncryptedVideoGroupViewHolder
import com.videoencrypter.viewholders.NormalVideoGroupViewHolder
import com.videoencrypter.vos.BaseVideoListVO
import com.videoencrypter.vos.EncryptedVideoListVO
import com.videoencrypter.vos.VideoListVO

class MainAdapter(private val context: Context) :
    BaseRecyclerAdapter<BaseVideoListVO, BaseViewHolder<BaseVideoListVO>>(context) {
    private val NORMAL_VIDEO_TYPE = 1
    private val ENCRYPTED_VIDEO_TYPE = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<BaseVideoListVO> {
        val view: View = mLayoutInflator.inflate(R.layout.cardview_group, parent, false)
        val delegate = context as VideoDelegate
        return if (viewType == NORMAL_VIDEO_TYPE) {
            NormalVideoGroupViewHolder(view,delegate) as BaseViewHolder<BaseVideoListVO>
        } else EncryptedVideoGroupViewHolder(view,delegate) as BaseViewHolder<BaseVideoListVO>
    }


    override fun getItemViewType(position: Int): Int {
        return when {
            mData!![position] is VideoListVO -> NORMAL_VIDEO_TYPE
            mData!![position] is EncryptedVideoListVO -> ENCRYPTED_VIDEO_TYPE
            else -> throw RuntimeException("NOT VALID VIEWTYPE")
        }
    }

}