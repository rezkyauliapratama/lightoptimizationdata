
package com.rezkyaulia.android.light_optimization_data.parser.parser

import com.google.gson.Gson
import com.google.gson.TypeAdapter

import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.Charset

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer

/**
 * Created by Rezky Aulia Pratama on 7/4/2017.
 */
internal class GReqBodyParserJson<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : ParserJson<T, RequestBody> {

    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        val jsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        jsonWriter.close()
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString())
    }

    private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
    private val UTF_8 = Charset.forName("UTF-8")
}
