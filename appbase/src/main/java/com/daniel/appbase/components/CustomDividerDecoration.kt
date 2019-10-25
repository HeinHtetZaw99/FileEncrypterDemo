package com.daniel.appbase.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CustomDividerDecoration : RecyclerView.ItemDecoration {
    private var defaultMargin = 24
    private var context: Context? = null
    private var divider: Drawable? = null

    /**
     * Default divider will be used
     */
    constructor(context: Context) {
        val styledAttributes = context.obtainStyledAttributes(ATTRS)
        divider = styledAttributes.getDrawable(0)
        this.context = context
        styledAttributes.recycle()
    }

    /**
     * Custom divider will be used
     */
    constructor(context: Context, resId: Int) {
        divider = ContextCompat.getDrawable(context, resId)
        this.context = context
    }

    fun setDefaultMargin(defaultMargin: Int) {
        this.defaultMargin = defaultMargin
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + convertToDP(defaultMargin)
        val right = parent.width - parent.paddingRight - convertToDP(defaultMargin)

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + divider!!.intrinsicHeight

            divider!!.setBounds(left, top, right, bottom)
            divider!!.draw(c)
        }
    }

    private fun convertToDP(unit: Int): Int {
        return (unit * context!!.resources.displayMetrics.density + 0.5f).toInt()
    }

    companion object {


        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }
}
