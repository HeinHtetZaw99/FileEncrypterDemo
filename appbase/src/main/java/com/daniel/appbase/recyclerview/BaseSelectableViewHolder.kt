package com.daniel.appbase.recyclerview


import android.view.View

import androidx.recyclerview.widget.RecyclerView




abstract class BaseSelectableViewHolder<W>(
    itemView: View,
    private val listener: RecyclerItemSelectedListener
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {


    init {
        itemView.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        listener.onSelected(adapterPosition)
    }

    abstract fun setData(mData: W, isSelected: Boolean)

    interface RecyclerItemSelectedListener {
        fun onSelected(selectedPosition: Int)
    }
}
