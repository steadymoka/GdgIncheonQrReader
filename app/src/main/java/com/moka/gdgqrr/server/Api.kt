package com.moka.gdgqrr.server

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable


object Api {

    val END_POINT = "http://endpoint.net"

    private var retrofit: Retrofit? = null

    val api: API
        get() {
            if (null == retrofit) {

                val builder = retrofitBuilder()
                builder.baseUrl(END_POINT)
                retrofit = builder.build()
            }
            return retrofit!!.create(API::class.java)
        }

    interface API {

        @GET("/v1/api/syncLocal.json")
        fun confirmEmail(@Query("email") email: String): Observable<Response<ConfirmEmailRes>>

    }

    private fun retrofitBuilder(): Retrofit.Builder {

        val builder = Retrofit.Builder()

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        builder.client(
                OkHttpClient
                        .Builder()
                        .addInterceptor(interceptor)
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        return builder
    }

    class ConfirmEmailRes

}