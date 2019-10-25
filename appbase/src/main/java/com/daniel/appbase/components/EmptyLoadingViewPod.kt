package com.daniel.appbase.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.daniel.appbase.R
import kotlinx.android.synthetic.main.empty_view_loading.view.*


class EmptyLoadingViewPod : RelativeLayout {
    /**
     * When Importing this class to use in another project, make sure to import other resources as well
     */
    private var onRefreshListener: OnRefreshListener? = null
    internal var context: Context
    private var currentState: EmptyViewState? = null


    constructor(context: Context) : super(context) {
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.context = context
    }

    fun setOnRefreshListener(onRefreshListener: OnRefreshListener) {
        this.onRefreshListener = onRefreshListener
    }

    fun setEmptyText(text: String) {
        emptyTv!!.text = text
    }

    fun hidePullToRefresh() {
        pullToRefreshBtn!!.visibility = View.GONE
        pullToRefreshTv!!.visibility = View.GONE
    }

    fun showPullToRefresh() {
        pullToRefreshBtn!!.visibility = View.VISIBLE
        pullToRefreshTv!!.visibility = View.VISIBLE
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.setCurrentState(EmptyViewState.INITIAL)
        pullToRefreshBtn!!.setOnClickListener {
            if (onRefreshListener != null)
                onRefreshListener!!.onRefreshButtonClicked()
        }
    }

    private fun toLoadingView() {
        errorView!!.visibility = View.GONE
        loadingRootView!!.visibility = View.VISIBLE
        loadingView!!.setAnimation("loading_animation.json")
        loadingView!!.playAnimation()
    }

    private fun toInitialState() {
        errorView!!.visibility = View.GONE
        if (loadingView!!.isAnimating)
            loadingView!!.pauseAnimation()
        loadingRootView!!.visibility = View.GONE
    }

    private fun toSearchView() {
        errorView!!.visibility = View.GONE
        loadingRootView!!.visibility = View.VISIBLE
        loadingView!!.setAnimation("search_products.json")
        loadingView!!.playAnimation()
    }


    private fun toErrorView() {
        errorView!!.visibility = View.VISIBLE
        if (loadingView!!.isAnimating)
            loadingView!!.pauseAnimation()
        loadingRootView!!.visibility = View.GONE

    }

    fun setCurrentState(currentState: EmptyViewState) {
        this.currentState = currentState
        when (currentState) {
            EmptyViewState.INITIAL -> {
                toInitialState()
            }
            EmptyViewState.LOADING -> {
                toLoadingView()
                pullToRefreshBtn!!.visibility = View.VISIBLE
            }
            EmptyViewState.SEARCHING -> {
                toSearchView()
                pullToRefreshBtn!!.visibility = View.VISIBLE
            }
            EmptyViewState.SEARCH_ERROR -> {
                toErrorView()
                emptyIv!!.setImageDrawable(context.getDrawable(R.drawable.ic_unexpected_error_v2))
                emptyTv!!.text = context.getString(R.string.title_msg_no_search_result)
                pullToRefreshTv!!.text = context.getString(R.string.title_msg_try_again_search)
                pullToRefreshBtn!!.visibility = View.GONE
            }
            EmptyViewState.NETWORK_ERROR -> {
                toErrorView()
                emptyIv!!.setImageDrawable(context.getDrawable(R.drawable.empty_view))
                emptyTv!!.text = context.getString(R.string.title_msg_emptyView)
                pullToRefreshTv!!.text = context.getString(R.string.title_msg_try_again)
                pullToRefreshBtn!!.visibility = View.VISIBLE
            }
            EmptyViewState.UNEXPECTED_ERROR -> {
                toErrorView()
                emptyIv!!.setImageDrawable(context.getDrawable(R.drawable.ic_unexpected_error_v2))
                emptyTv!!.text = context.getString(R.string.title_msg_unexpected_error_occured)
                pullToRefreshTv!!.text = context.getString(R.string.title_msg_try_again_for_error)
                pullToRefreshBtn!!.visibility = View.VISIBLE

            }
        }
    }

    enum class EmptyViewState {
        NETWORK_ERROR,
        LOADING,
        UNEXPECTED_ERROR,
        SEARCHING,
        INITIAL,
        SEARCH_ERROR
    }

    interface OnRefreshListener {
        fun onRefreshButtonClicked()
    }

}
