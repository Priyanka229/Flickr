package com.app.flickr

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewLoadMoreListener protected constructor(private val mLinearLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    private var previousTotal = 0
    private var currentPage = 1
    private var loading = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val totalItemCount = mLinearLayoutManager.itemCount
        val lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition()

        if (loading && totalItemCount > previousTotal) {
            loading = false
            previousTotal = totalItemCount
        }

        if (!loading && totalItemCount >= currentPage * PER_PAGE_COUNT
            && totalItemCount - lastVisibleItemPosition <= OFFSET
        ) {
            currentPage++
            onLoadMore(currentPage)
            loading = true
        }
    }

    fun resetPreviousTotal() {
        previousTotal = 0
        currentPage = 1
    }

    abstract fun onLoadMore(currentPage: Int)

    companion object {
        const val OFFSET = 10
        const val PER_PAGE_COUNT = 20
    }
}