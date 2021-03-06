package com.jaus.albertogiunta.teseo.kotlin.utils

import android.os.Build
import com.jaus.albertogiunta.teseo.kotlin.data.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi


object MsgFromWebsocket {
    const val NORMAL_CONNECTION_RESPONSE = "ack"
    const val END_ALARM = "endAlarm"
    const val SYS_SHUTDOWN = "sysShutdown"
}

object MsgToWebsocket {
    const val FIRST_CONNECTION = "firstConnection"
    const val NORMAL_CONNECTION = "normalConnection"
    const val ALARM_SETUP = "okToReceiveAlarms"
}

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

    fun roomFromUri(id: Int, area: AreaViewedFromAUser): RoomViewedFromAUser =
            area.rooms.first { r -> r.info.id.serial == id }

    fun getSerialFromIP(str: String): Int = str.split("uri").last().toInt()
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