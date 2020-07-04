package com.app.flickr

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.flickr.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main) }

    private lateinit var viewModel: FlikrVM
    private var flickrAdapter: FlickrAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** setup view */
        setUpView()
    }

    private fun setUpView() {
        var searchedKeyword = ""
        binding.apply {
            /** recycler view set up */
            flickrAdapter = FlickrAdapter()
            val llm = GridLayoutManager(this@MainActivity, 3)
            val loadMoreListener = object : RecyclerViewLoadMoreListener(llm) {
                override fun onLoadMore(currentPage: Int) {
                    binding.progress.visibility = View.VISIBLE
                    closeKeyboard()
                    viewModel.getFlickrData(searchedKeyword, currentPage)
                }

            }
            flickrRv.apply {
                layoutManager = llm
                adapter = flickrAdapter
                addOnScrollListener(loadMoreListener)
            }


            /** search listeners */
            val searchDelayHandler = SearchDelayHandler().apply {
                afterWaitingCall = { keyword ->
                    if (keyword.isBlank().not()) {
                        searchedKeyword = keyword

                        closeKeyboard()
                        binding.progress.visibility = View.VISIBLE
                        flickrAdapter?.submitList(mutableListOf())
                        loadMoreListener.resetPreviousTotal()

                        viewModel.getFlickrData(searchedKeyword, 1)
                    } else {
                        Toast.makeText(this@MainActivity, "Please enter something to search", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            flickrSearch.addTextChangedListener { searchDelayHandler.startWaiting(it.toString().trim()) }
        }

        /** set up view model and observer */
        viewModel = ViewModelProvider(this).get(FlikrVM::class.java)

        /** setup observers */
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.flickrLiveData.observe(this, Observer {
            binding.progress.visibility = View.GONE
            flickrAdapter?.submitList(it.toMutableList())
        })

        viewModel.errorLiveData.observe(this, Observer {
            Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
        })
    }

    private class SearchDelayHandler: Handler() {
        companion object {
            const val DELAY = 1000L
        }
        var keyword: String = ""
        var afterWaitingCall: ((count: String) -> Unit)? = null
        val actionAfterWaiting = Runnable { afterWaitingCall?.invoke(keyword) }

        fun startWaiting(keyword: String) {
            removeCallbacksAndMessages(null)
            this.keyword = keyword
            postDelayed(actionAfterWaiting, DELAY)
        }
    }

    fun closeKeyboard() {
        val inputManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        var view1 = currentFocus
        if (view1 == null) {
            view1 = View(this)
        }
        inputManager.hideSoftInputFromWindow(
            view1.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}
