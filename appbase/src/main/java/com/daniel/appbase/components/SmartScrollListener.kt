package com.daniel.appbase.components

import android.view.View

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * This class controls not only whether the list has reached to its end
 * or whether the list has reached to its top most
 */
class SmartScrollListener(
    private val mSmartScrollListener: OnSmartScrollListener,
    private val swipeRefreshLayout: SwipeRefreshLayout?
) : RecyclerView.OnScrollListener() {

    private var isListEndReached = false
    private var newItemAlert: View? = null
    private var limit = 10
    private var thresholdItemCount = 7

    fun setThresholdItemCount(thresholdItemCount: Int) {
        this.thresholdItemCount = thresholdItemCount + 1
    }


    /**
     * for adding a button or text for showing more items down the list,
     * It will be hidden when user scroll down the list manually
     */
    fun setNewItemsAlert(newItemAlert: View) {
        this.newItemAlert = newItemAlert
    }

    override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(rv, dx, dy)

        val visibleItemCount = rv.layoutManager!!.childCount
        val totalItemCount = rv.layoutManager!!.itemCount
        val pastVisibleItems =
            (rv.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

        /** User had scrolled down to the new items , so make the @newItemAlert invisible */
        if (newItemAlert != null && pastVisibleItems + visibleItemCount > limit && newItemAlert!!.visibility == View.VISIBLE) {
            newItemAlert!!.visibility = View.GONE
        }
        if (visibleItemCount + pastVisibleItems < totalItemCount) {
            isListEndReached = false
        }


        if (swipeRefreshLayout != null)
            if ((rv.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0) {
                // Its at top, so enable to do the swipe refresh action
                swipeRefreshLayout.isEnabled = true
            } else
                swipeRefreshLayout.isEnabled = false
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {
        super.onScrollStateChanged(recyclerView, scrollState)
        /*  if (scrollState == RecyclerView.SCROLL_STATE_IDLE
                && ((LinearLayoutManager) recyclerView.getLayoutManager())
                .findLastCompletelyVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1
                && !isListEndReached) {*/
        if (!recyclerView.canScrollVertically(1) && !isListEndReached) {
            isListEndReached = true
            limit = recyclerView.layoutManager!!.itemCount
            mSmartScrollListener.onListEndReach()
        }
    }

    interface OnSmartScrollListener {
        fun onListEndReach()
    }
}
