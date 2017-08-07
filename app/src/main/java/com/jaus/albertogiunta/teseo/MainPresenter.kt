package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.AreaViewedFromAUser
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.data.RoomViewedFromAUser
import com.jaus.albertogiunta.teseo.data.RouteResponseShort
import com.jaus.albertogiunta.teseo.util.*
import trikita.log.Log

class MainPresenter(val view: View) : AreaUpdateListener, UserMovementListener, UserPositionListener, SignalListener, Receivers {

    companion object {
        const val FIRST_CONNECTION = "firstconnection"
        const val NORMAL_CONNECTION = "connect"
        const val NORMAL_CONNECTION_RESPONSE = "ack"
    }

    var isSwitching: Boolean = false

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
            cellObservers.forEach { o ->
                value?.let {
                    Log.d(": $it")
                    o.onCellUpdated(it)
                }
            }
        }

    var position: Point = Point(1, 1)
        set(value) {
            field = value
            positionObservers.forEach { o -> o.onPositionChanged(value) }
        }

    var route: RouteResponseShort? = null
        set(value) {
            field = value
            value?.let { view.onRouteReceived(Unmarshalers.roomsInfoListFromIDs(it.route, area!!), false) }
        }

    var emergencyRoute: RouteResponseShort? = null
        set(value) {
            field = value
            value?.let { view.onRouteReceived(Unmarshalers.roomsInfoListFromIDs(it.route, area!!), true) }
        }


    init {
        cellObservers.add(view)
        cellObservers.add(signal)
        positionObservers.add(this)
        positionObservers.add(signal)
        positionObservers.add(view)
    }

    override fun onMovementDetected(direction: Direction) {
        if (!isSwitching) {
            val tempPosition = direction.operationOnPoint(position)
            cell?.let {
                if (MovementHelper.isMovementLegit(tempPosition, (cell as RoomViewedFromAUser).info.roomVertices, (cell as RoomViewedFromAUser).passages)) {
                    position = tempPosition
                } else {
                    Log.d("onMovementDetected: movement was not legit for temp $tempPosition ||| position $position ||| direction $direction")
                }
            }
        }
    }

    override fun onPositionChanged(userPosition: Point) {
        Log.d("onPositionChanged: " + route?.route?.last()?.serial + " " + cell?.info?.id?.serial)
        if (isSetupFinished() && route?.route?.last()?.serial == cell?.info?.id?.serial) {
            view.onRouteFollowedUntilEnd()
            route = null
        }
    }

    override fun onAreaUpdated(area: AreaViewedFromAUser) {
        this.area = area
    }

    override fun onConnectMessageReceived(text: String?) {
        Log.d("onConnectMessageReceived: received message")
        text?.let {
            if (text == NORMAL_CONNECTION_RESPONSE && isSetupFinished()) {
                cell = area?.rooms?.filter { (infoCell) -> infoCell.id.serial == signal.bestNewCandidate.info.id.serial }?.first()
                isSwitching = false
                Log.d("onConnectMessageReceived: cellId" + cell?.info?.id)
            } else if (text != NORMAL_CONNECTION_RESPONSE && !isSetupFinished()) {
                onAreaUpdated(Unmarshalers.unmarshalArea(it))
                cell = area?.rooms?.filter({ (info) -> info.isEntryPoint })?.first()
                Log.d("onConnectMessageReceived: cellId " + cell?.info?.id)
            } else {
                Log.d("onConnectMessageReceived: $text")
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
//        if (area == null) onConnectMessageReceived(BufferedReader(InputStreamReader(view.context().resources.openRawResource(R.raw.area))).lines().collect(Collectors.joining("\n")))
//        else onConnectMessageReceived(NORMAL_CONNECTION_RESPONSE)
        // use in production
        webSocketHelper.connectWS.send(if (area == null) FIRST_CONNECTION else NORMAL_CONNECTION)
    }

    fun askRoute(departureRoomName: String, arrivalRoomName: String) {
        val depId: Int = area!!.rooms.filter { (info) -> info.id.name == departureRoomName }.map { (info) -> info.id.serial }.first()
        val arrId: Int = area!!.rooms.filter { (info) -> info.id.name == arrivalRoomName }.map { (info) -> info.id.serial }.first()
        Log.d("askRoute: uri$depId-uri$arrId")
        area?.let { webSocketHelper.routeWS.send("uri$depId-uri$arrId") }
    }

    override fun onSwitchToCellRequested(room: RoomViewedFromAUser) {
        // use for test
//        onConnectMessageReceived(NORMAL_CONNECTION_RESPONSE)
        // use in production
        isSwitching = true
        webSocketHelper.handleSwitch(room.cell)
    }

    override fun onSignalStrengthUpdated(strength: SIGNAL_STRENGTH) {
        view.onSignalStrengthUpdated(strength)
    }

    fun isSetupFinished(): Boolean {
        return area != null &&
                cell != null
    }
}