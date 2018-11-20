package com.rezkyaulia.android.light_optimization_data

/**
 * Created by Rezky Aulia Pratama on 7/4/2017.
 */

interface NetworkBuilder {

    fun addQueryParameter(key: String, value: String): NetworkBuilder

}
