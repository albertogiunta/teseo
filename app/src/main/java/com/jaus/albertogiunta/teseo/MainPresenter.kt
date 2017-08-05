package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.AreaViewedFromAUser
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.data.RoomViewedFromAUser
import com.jaus.albertogiunta.teseo.data.RouteResponseShort
import com.jaus.albertogiunta.teseo.util.*
import trikita.log.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

class MainPresenter(val view: View) : AreaUpdateListener, UserMovementListener, UserPositionListener, SignalListener, Receivers, CellUpdateListener {

    companion object {
        const val FIRST_CONNECTION = "firstconnection"
        const val NORMAL_CONNECTION = "connect"
        const val NORMAL_CONNECTION_RESPONSE = "ack"
    }

    var signal: SignalHelper = SignalHelper(this)

    var webSocketHelper = WebSocketHelper(this)

    var area: AreaViewedFromAUser? = null
        set(value) {
            field = value
            value?.let {
                view.onAreaUpdated(it)
//                var entryPosition: Point = value
//                        .rooms.filter { (info) -> info.isEntryPoint }.first()
//                        .passages.filter { (neighborId) -> neighborId == 0 }
//                        .map { p -> p.startCoordinates }.first()
//                entryPosition.x += 1
                var entryPosition: Point = value
                        .rooms.filter { (info) -> info.isEntryPoint }.first()
                        .info.antennaPosition
                position = entryPosition
            }
        }

    var positionObservers: MutableList<UserPositionListener> = mutableListOf()
    var cellObservers: MutableList<CellUpdateListener> = mutableListOf()

    var cell: RoomViewedFromAUser? = null
        set(value) {
            field = value
            value?.let { cellObservers.forEach { o -> o.onCellUpdated(it) } }
        }

    var position: Point = Point(1, 1)
        set(value) {
            field = value
            positionObservers.forEach { o -> o.onPositionChanged(value) }
        }

    var route: RouteResponseShort? = null
        set(value) {
            field = value
            value?.let { view.onRouteReceived(Unmarshalers.roomsInfoListFromIDs(it.route, area!!)) }
        }

    var emergencyRoute: RouteResponseShort? = null
        set(value) {
            field = value
            value?.let { view.onEmergencyRouteReceived(Unmarshalers.roomsInfoListFromIDs(it.route, area!!)) }
        }


    init {
        cellObservers.add(this)
        cellObservers.add(signal)
        positionObservers.add(signal)
        positionObservers.add(view)
        positionObservers.add(this)
    }

    override fun onMovementDetected(direction: Direction) {
        val tempPosition = direction.operationOnPoint(position)
        cell?.let {
            if (MovementHelper.isMovementLegit(tempPosition, (cell as RoomViewedFromAUser).info.roomVertices, (cell as RoomViewedFromAUser).passages)) {
                position = tempPosition
            } else {
                Log.d("onMovementDetected: movement was not legit for temp $tempPosition ||| position $position ||| direction $direction")
            }
        }
    }

    override fun onPositionChanged(userPosition: Point) {
//        Log.d("onPositionChanged: " + route?.route?.last()?.id + " " + cell?.info?.id)
//        if (route?.route?.last()?.serial == cell?.info?.id?.serial) {
//            view.onRouteFollowedUntilEnd()
//            route = null
//        }
    }

    override fun onAreaUpdated(area: AreaViewedFromAUser) {
        this.area = area
    }

    override fun onCellUpdated(cell: RoomViewedFromAUser) {
        view.onCellUpdated(cell.info.id.name)
    }

    override fun onConnectMessageReceived(text: String?) {
        Log.d("onConnectMessageReceived: received message")
        text?.let {
            if (text == NORMAL_CONNECTION_RESPONSE) {
                cell = area?.rooms?.filter { (infoCell) -> infoCell.id.serial == signal.bestNewCandidate.info.id.serial }?.first()
                Log.d("onConnectMessageReceived: " + cell?.info?.id)
            } else {
                onAreaUpdated(Unmarshalers.unmarshalArea(it))
                cell = area?.rooms?.filter({ (info) -> info.isEntryPoint })?.first()
            }
        }
    }

    override fun onAlarmMessageReceived(text: String?) {
        Log.d("onAlarmMessageReceived: received ALARM")
        text?.let { emergencyRoute = Unmarshalers.unmarshalRouteResponse(it) }
    }

    override fun onRouteMessageReceived(text: String?) {
        Log.d("onRouteMessageReceived: received ROUTE")
        text?.let { route = Unmarshalers.unmarshalRouteResponse(it) }
    }

    fun askConnection() {
        // use for test
        if (area == null) onConnectMessageReceived(BufferedReader(InputStreamReader(view.context().resources.openRawResource(R.raw.area))).lines().collect(Collectors.joining("\n")))
        else onConnectMessageReceived(NORMAL_CONNECTION_RESPONSE)
        // use in production
//        webSocketHelper.connectWS.send(if (area == null) FIRST_CONNECTION else NORMAL_CONNECTION)
    }

    fun askRoute() {
        Log.d("askRoute: ${area?.rooms?.first()?.info?.id}-${area?.rooms?.last()?.info?.id}")
        area?.let { webSocketHelper.routeWS.send("uri${it.rooms.first().info.id.serial}-uri${it.rooms.last().info.id.serial}") }
    }

    override fun onSwitchToCellRequested(room: RoomViewedFromAUser) {
        // use for test
        onConnectMessageReceived(NORMAL_CONNECTION_RESPONSE)
        // use in production
//        webSocketHelper.handleSwitch(room.cell)
    }

    override fun onSignalStrengthUpdated(strength: SIGNAL_STRENGTH) {
        view.onSignalStrengthUpdated(strength)
    }
}