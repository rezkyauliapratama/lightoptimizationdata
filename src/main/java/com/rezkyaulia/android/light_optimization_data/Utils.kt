package com.rezkyaulia.android.light_optimization_data

import android.content.Context

import java.io.File

import okhttp3.Cache

/**
 * Created by Rezky Aulia Pratama on 7/4/2017.
 */

object Utils{
    fun getCache(context: Context, maxCacheSize: Int, uniqueName: String): Cache {
        return Cache(getDiskCacheDir(context, uniqueName), maxCacheSize.toLong())
    }

    private fun getDiskCacheDir(context: Context, uniqueName: String): File {
        return File(context.cacheDir, uniqueName)
    }


}
