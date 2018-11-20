package com.rezkyaulia.android.light_optimization_data


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rezkyaulia.android.light_optimization_data.RequestListener.ParsedCallback
import com.rezkyaulia.android.light_optimization_data.parser.parser.ParseFactory
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single

import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.util.ArrayList

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import android.util.Log
import androidx.annotation.WorkerThread
import io.reactivex.disposables.Disposable


@Suppress("UNCHECKED_CAST")
/**
 * Created by Rezky Aulia Pratama on 7/4/2017.
 */
class  HttpCore<T> : Observable<T> {
    override fun subscribeActual(observer: Observer<in T>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private val JSON = MediaType.parse("application/json; charset=utf-8")

    private var mClient: OkHttpClient
    private var mURL: String
    private var mClass: Class<*>
    private var mHeader: Headers? = null
    private var mTag: String? = null
    private var mRequestBody: StringBuilder? = null
    private val parts = ArrayList<Part>()


    private var mGson: Gson = provideGsonBuilder().create()

    private var mMethod: String? = null

    internal var t: T? = null
    internal var e: IOException? = null
    private//                    MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
    val request: Request
        get() {
            val requestBuilder = provideRequestBuilder()

            if (mHeader != null)
                requestBuilder.headers(mHeader!!)

            if (mTag != null)
                requestBuilder.tag(mTag!!)

            var requestBody = RequestBody.create(JSON, if (mRequestBody == null) "" else mRequestBody.toString())
            if (parts.isNotEmpty()) {
                if (parts.size > 0) {
                    var builder: MultipartBody.Builder = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                    for ((key, file) in parts) {
                        val mediaType = MediaType.parse("application/octet-stream")
                        val body = RequestBody.create(mediaType, file)
                        builder = builder.addFormDataPart(key, file.name, body)
                    }
                    if (mRequestBody != null)
                        builder.addPart(requestBody)
                    requestBody = builder.build()
                }
            }

            if (mMethod == null) {
                if (mRequestBody == null) {
                    requestBuilder.get().build()
                } else {
                    requestBuilder.post(requestBody).build()
                }
            } else {
                requestBuilder.method(mMethod!!.toUpperCase(), requestBody).build()
            }
            return requestBuilder.build()
        }


    val syncFuture: T?
        @WorkerThread
        @Throws(IOException::class)
        get() {

            if (mURL.isEmpty()) {
                throw IOException("URL is null")
            }

            val request = request

                val response = mClient.newCall(request).execute()
                if (!response.isSuccessful) {
                    e = IOException("Unexpected code $response")
                    throw e as IOException
                }

                t = response.body()?.let { ParseFactory.PARSER_JSON_FACTORY.responseBodyParser(mClass).convert(it) } as T

                try {
                    response.body()?.close()
                } catch (ignored: Exception) {
                    throw ignored
                }

            return t
        }

    val asString: HttpCore<*>
        get() {
            mClass = String::class.java
            val request = provideRequestBuilder()
                    .build()

                mClient.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        t = response.body()?.string() as T
                    }
                })

            return this
        }


    //private constructor
    constructor(client: OkHttpClient, aClass: Class<T>, url: String) {
        this.mClient = client
        this.mClass = aClass
        this.mURL = url
        this.mMethod = null

    }

    //private constructor
    constructor(client: OkHttpClient, aClass: Class<T>, url: String, method: String) {
        this.mClient = client
        this.mClass = aClass
        this.mURL = url
        this.mMethod = method
    }

    private fun provideGsonBuilder(): GsonBuilder {
        return GsonBuilder()
    }

    //private constructor
    constructor(client: OkHttpClient, type: Type, url: String, method: String) {
        this.mClient = client
        this.mClass = type.javaClass
        this.mURL = url
        this.mMethod = method

        val builder = GsonBuilder()
    }

    fun setHeaders(headers: Headers): HttpCore<T> {
        this.mHeader = headers
        return this
    }

    fun setTag(tag: String): HttpCore<T> {
        this.mTag = tag
        return this
    }

    fun setMultipartFile(key: String, file: File): HttpCore<T> {

        if (mMethod == null) {
            this.mMethod = NetworkClient.POST
        }
        parts.add(Part(key, file))

        return this
    }

    fun setJsonPojoBody(request: Any): HttpCore<T> {
        mRequestBody = StringBuilder()
        mGson.toJson(request, mRequestBody)
        return this
    }

    private fun provideRequestBuilder(): Request.Builder {
        return Request.Builder()
                .url(mURL)
    }


    fun getAsFuture(callback: ParsedCallback<T>): HttpCore<T> {

        if (mURL.isEmpty()) {
            callback.onFailure(IOException("URL is null"))
            return this
        }

        val request = request

            mClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, ex: IOException) {
                    e = ex
                    callback.onFailure(e!!)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (mClass != String::class.java) {
                        t = response.body()?.let { ParseFactory.PARSER_JSON_FACTORY.responseBodyParser(mClass).convert(it) } as T
                    } else {
                        t = response.body()?.string() as T
                    }
                    if (t != null) {
                        callback.onCompleted(t as T)
                    }else{
                        e = IOException("response is null")
                        callback.onFailure(e as IOException)
                    }
                }
            })

        return this
    }


    fun getAsObservable() : Observable<T>{

        val request = request
        val call = mClient.newCall(request)

        val subscription =  Observable.create<T> { emitter ->
            try {

                val response =call.execute()
                if (!response.isSuccessful) {
                    e = IOException("Unexpected code $response")
                    emitter.onError(e as IOException)
                }

                t = response.body()?.let { ParseFactory.PARSER_JSON_FACTORY.responseBodyParser(mClass).convert(it) } as T

                response.body()?.close()

                emitter.onNext(t as T)
            } catch (e: Exception) {
                if (!emitter.isDisposed)
                    emitter.onError(e)            }
        }



        return subscription.doOnDispose {
            Log.e(HttpCore::class.java.simpleName, "dispose")
            if (!call.isCanceled)
                call.cancel()
            Log.e(HttpCore::class.java.simpleName, "call.cancel() : "+call.isCanceled)        }
    }



    fun getAsSingle() : Single<T>{
        val request = request
        val call = mClient.newCall(request)

        val subscription =  Single.create<T> { emitter ->
            try{
                val response = call.execute()
                if (!response.isSuccessful) {
                    e = IOException("Unexpected code $response")
                    emitter.onError(e as IOException)
                }

                t = response.body()?.let { ParseFactory.PARSER_JSON_FACTORY.responseBodyParser(mClass).convert(it) } as T

                response.body()?.close()


                emitter.onSuccess(t as T)
            } catch (e: Exception) {
                if (!emitter.isDisposed)
                    emitter.onError(e)
            }

        }

        return subscription.doOnDispose {
            Log.e(HttpCore::class.java.simpleName, "dispose")
            if (!call.isCanceled)
                call.cancel()
            Log.e(HttpCore::class.java.simpleName, "call.cancel() : "+call.isCanceled)        }
    }


    class  ANDisposable  constructor(private val call: Call) : Disposable {
        override fun isDisposed(): Boolean {
            return call.isCanceled
        }


        override fun dispose() {
            Log.e(HttpCore::class.java.simpleName, "dispose")
            this.call.cancel()
            Log.e(HttpCore::class.java.simpleName, "call.cancel() : "+call.isCanceled)

        }
    }
}
