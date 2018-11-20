package com.rezkyaulia.android.light_optimization_data.parser.RequestListener

import java.io.IOException

/**
 * Created by Rezky Aulia Pratama on 20/10/2016.
 */
interface ParsedCallback<T> {
    fun onCompleted(result: T)
    fun onFailure(e: IOException)


}
