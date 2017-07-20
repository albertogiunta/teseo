package com.jaus.albertogiunta.teseo.util

import com.jaus.albertogiunta.teseo.data.Area
import com.jaus.albertogiunta.teseo.data.RouteResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

object Unmarshalers {

    fun unmarshalArea(string: String): Area {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Area> = moshi.adapter(Area::class.java)
//        return jsonAdapter.fromJson(AreaJsonForTeset.json) as Area
        return jsonAdapter.fromJson(string) as Area
    }

    fun unmarshalMap(string: String): RouteResponse {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<RouteResponse> = moshi.adapter(RouteResponse::class.java)
        return jsonAdapter.fromJson(string) as RouteResponse
    }

}