package com.app.flickr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.app.flickr.RecyclerViewLoadMoreListener.Companion.PER_PAGE_COUNT
import com.app.flickr.network.FlickrApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlikrVM(val app: Application): AndroidViewModel(app) {
    val dataList = mutableListOf<Photo>()
    val flickrLiveData = MutableLiveData<MutableList<Photo>>()
    val errorLiveData = MutableLiveData<String>()

    companion object {
        const val FLICKR_METHOD = "flickr.photos.search"
        const val FLICKR_API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736"
        const val FLICKR_FORMAT = "json"
        const val FLICKR_CALLBACK = 1
    }

    fun getFlickrData(keyword: String, pageNo: Int) {
        val call = FlickrApiClient.service.getData(keyword,
            pageNo, PER_PAGE_COUNT, FLICKR_METHOD, FLICKR_FORMAT, FLICKR_CALLBACK, FLICKR_API_KEY)
        call.enqueue(object : Callback<FlickrRes> {
            override fun onFailure(call: Call<FlickrRes>, t: Throwable) {
                errorLiveData.postValue(t.message)
            }

            override fun onResponse(call: Call<FlickrRes>, response: Response<FlickrRes>) {
                val flickrRes = response.body()
                val photos = flickrRes?.photos?.photo
                if (photos?.isNullOrEmpty()?.not() == true) {
                    if (pageNo == 1) {
                        dataList.clear()
                    }
                    dataList.addAll(photos)
                    flickrLiveData.postValue(dataList)
                }
            }

        })
    }
}