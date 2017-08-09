package com.jaus.albertogiunta.teseo.util

import com.jaus.albertogiunta.teseo.data.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

object AreaState {

    var area: AreaViewedFromAUser? = null

}

object Unmarshaler {

    val moshi: Moshi = Moshi.Builder().build()

    fun unmarshalArea(string: String): AreaViewedFromAUser {
        val jsonAdapter: JsonAdapter<AreaViewedFromAUser> = moshi.adapter(AreaViewedFromAUser::class.java)
        AreaState.area = jsonAdapter.fromJson(string) as AreaViewedFromAUser
        return jsonAdapter.fromJson(string) as AreaViewedFromAUser
    }

    fun unmarshalRouteResponse(string: String): RouteResponseShort {
        val jsonAdapter: JsonAdapter<RouteResponseShort> = moshi.adapter(RouteResponseShort::class.java)
        return jsonAdapter.fromJson(string) as RouteResponseShort
    }

    fun roomsListFromIDs(ids: List<RoomID>, area: AreaViewedFromAUser): List<RoomViewedFromAUser> {
        return ids.filter { (serial) -> serial > 0 }.map { (serial) -> serial }.map { id -> area.rooms.first { (info) -> info.id.serial == id } }
    }

    fun roomsInfoListFromIDs(ids: List<RoomID>, area: AreaViewedFromAUser): List<RoomInfo> {
        return roomsListFromIDs(ids, area).map { (info) -> info }
    }
}