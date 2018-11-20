package com.rezkyaulia.android.light_optimization_data.parser.parser

import java.io.IOException
import java.lang.reflect.Type
import java.util.HashMap

import okhttp3.RequestBody
import okhttp3.ResponseBody

/**
 * Created by Rezky Aulia Pratama on 7/4/2017.
 */
interface ParserJson<F, T> {

    @Throws(IOException::class)
    abstract fun convert(value: F): T

    abstract class Factory {

        abstract fun responseBodyParser(type: Type): ParserJson<ResponseBody, *>

        abstract fun requestBodyParser(type: Type): ParserJson<*, RequestBody>

        abstract fun getObject(string: String, type: Type): Any

        abstract fun getString(`object`: Any): String

        abstract fun getStringMap(`object`: Any): HashMap<String, String>

    }

}