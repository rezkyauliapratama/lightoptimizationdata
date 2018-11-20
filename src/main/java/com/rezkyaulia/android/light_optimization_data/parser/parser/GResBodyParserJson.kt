package com.rezkyaulia.android.light_optimization_data.parser.parser

import com.google.gson.Gson
import com.google.gson.TypeAdapter

import java.io.IOException

import okhttp3.ResponseBody

/**
 * Created by Rezky Aulia Pratama on 7/4/2017.
 */
internal class GResBodyParserJson<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : ParserJson<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val jsonReader = gson.newJsonReader(value.charStream())
        value.use {
            return adapter.read(jsonReader)
        }
    }
}
