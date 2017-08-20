package com.jaus.albertogiunta.teseo.utils

import android.os.Build
import com.jaus.albertogiunta.teseo.data.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi


object EmulatorUtils {

    /**
     * Find out if your on emulator or you
     */
    fun isOnEmulator(): Boolean = Build.HARDWARE.contains("golfdish")

}

object IDExtractor {
    fun roomsListFromIDs(ids: List<RoomID>, area: AreaViewedFromAUser): List<RoomViewedFromAUser> =
            ids.filter { (serial) -> serial > 0 }.map { (serial) -> serial }.map { id -> area.rooms.first { (info) -> info.id.serial == id } }

    fun roomsInfoListFromIDs(ids: List<RoomID>, area: AreaViewedFromAUser): List<RoomInfo> =
            roomsListFromIDs(ids, area).map { (info) -> info }
}

object Unmarshaler {

    private val moshi: Moshi = Moshi.Builder().build()

    fun unmarshalArea(unmarshaledArea: String): AreaViewedFromAUser {
        val jsonAdapter: JsonAdapter<AreaViewedFromAUser> = moshi.adapter(AreaViewedFromAUser::class.java)
        AreaState.area = jsonAdapter.fromJson(unmarshaledArea) as AreaViewedFromAUser
        return AreaState.area!!
    }

    fun unmarshalRouteResponse(unmarshaledRouteResponseShort: String): RouteResponseShort {
        val jsonAdapter: JsonAdapter<RouteResponseShort> = moshi.adapter(RouteResponseShort::class.java)
        return jsonAdapter.fromJson(unmarshaledRouteResponseShort) as RouteResponseShort
    }
}