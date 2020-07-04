package com.app.flickr.network

import com.app.flickr.FlickrRes
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
interface FlickrApiService {
    @GET("services/rest")
    fun getData(@Query("text") text: String,
                            @Query("page") pageNo: Int,
                            @Query("per_page") perPage: Int,
                            @Query("method") method: String,
                            @Query("format") format: String,
                            @Query("nojsoncallback") callback: Int,
                            @Query("api_key") apiKey: String
                            ) : Call<FlickrRes>
}