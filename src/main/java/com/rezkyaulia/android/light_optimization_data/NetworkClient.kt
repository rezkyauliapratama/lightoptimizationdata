package com.rezkyaulia.android.light_optimization_data

import android.content.Context
import androidx.annotation.NonNull
import androidx.annotation.StringDef


import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by Rezky Aulia Pratama on 7/4/2017.
 */

class NetworkClient(context: Context) {

    private var sHttpClient: OkHttpClient? = OkHttpClient().newBuilder()
            .cache(Utils.getCache(context, NConstant.MAX_CACHE_SIZE, NConstant.CACHE_DIR_NAME))
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    constructor(context: Context,@NonNull okHttpClient: OkHttpClient) : this(context) {
        this.sHttpClient = okHttpClient
    }

    @StringDef(GET, POST, PUT, DELETE)
    @Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
    @kotlin.annotation.Retention
    @MustBeDocumented
    annotation class METHOD

    fun cancelAllRequest() {
        sHttpClient?.let {
            for (call in it.dispatcher().queuedCalls()) {
                call.cancel()
            }

            for (call in it.dispatcher().runningCalls()) {
                call.cancel()
            }
        }

    }

    fun cancelByTag(tag: String) {
        sHttpClient?.let {
            for (call in it.dispatcher().queuedCalls()) {
                if (call.request().tag() == tag)
                    call.cancel()
            }

            for (call in it.dispatcher().runningCalls()) {
                if (call.request().tag() == tag)
                    call.cancel()
            }
        }

    }

    fun withUrl(url: String): InitHttpCore {
        return InitHttpCore(url)
    }

    inner class InitHttpCore(private var mURL: String) {

        /**
         * initAs a standard method to initialize Http Connection.
         *
         * @param t The class that representation of response value.
         */
        @Throws
        fun <T> initAs(t: Class<T>): HttpCore<T> {

            if (sHttpClient == null) {
                throw IOException("OkhttpClient is null")
            }
            return HttpCore(sHttpClient as OkHttpClient, t, mURL)
        }

        /**
         * Make a standard toast that just contains a text view.
         *
         * @param t The class that representation of response value.
         * @param method Http method for this request.  Either [.GET] or
         * [.POST] or [.PUT] or [.DELETE]
         */
        @Throws
        fun <T> initAs(t: Class<T>, @METHOD method: String): HttpCore<T> {

            if (sHttpClient == null) {
                throw IOException("OkhttpClient is null")
            }
            return HttpCore(sHttpClient as OkHttpClient, t, mURL, method)
        }

    }

    companion object {

        const val POST = "POST"

        const val GET = "GET"
        const val PUT = "PUT"
        const val DELETE = "DELETE"
    }
}
