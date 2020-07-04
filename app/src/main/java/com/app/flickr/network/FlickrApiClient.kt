package com.app.flickr.network

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object FlickrApiClient {
    const val API_URL = "https://api.flickr.com/"
    var headers =  HashMap<String, String>()

    private val httpClient =
            OkHttpClient.Builder()
                    .addInterceptor(object : Interceptor {
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val original = chain.request()

                            val request = original.newBuilder()
                                    .headers(with(Headers.Companion) { headers.toHeaders() })
                                    .method(original.method, original.body)
                                    .build()
                            return chain.proceed(request)
                        }
                    })
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .build()


    var service: FlickrApiService = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(API_URL)
            .client(httpClient)
            .build().create(FlickrApiService::class.java)
}