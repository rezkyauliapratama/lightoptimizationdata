package com.rezkyaulia.android.light_optimization_data.parser.parser

import com.google.gson.Gson

/**
 * Created by Rezky Aulia Pratama on 7/4/2017.
 */
class ParseFactory {
companion object {
    val PARSER_JSON_FACTORY: ParserJson.Factory = GParserFactory(Gson())
}
}
