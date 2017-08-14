package com.jaus.albertogiunta.teseo.screens.areaNavigation

import com.jaus.albertogiunta.teseo.*
import com.jaus.albertogiunta.teseo.data.*
import com.jaus.albertogiunta.teseo.helpers.*
import com.jaus.albertogiunta.teseo.networking.WSMessageCallbacks
import com.jaus.albertogiunta.teseo.networking.WebSocketHelper
import com.jaus.albertogiunta.teseo.util.IDExtractor
import com.jaus.albertogiunta.teseo.util.Unmarshaler
import trikita.log.Log

class MainPresenter(val view: View) : AreaUpdateListener, UserMovementListener, UserPositionListener, SignalAndCellSwitchingListener, WSMessageCallbacks {

    companion object {
        const val FIRST_CONNECTION = "firstConnection"
        const val NORMAL_CONNECTION = "normalConnection"
        const val DISCONNECTION = "disconnect"
        const val NORMAL_CONNECTION_RESPONSE = "ack"
    }

    var isSwitching: Boolean = false

    var signal: SignalHelper = SignalHelper(this)

    var webSocketHelper = WebSocketHelper(this)

    var signalObservers: MutableList<SignalListener> = mutableListOf()
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

    var position: Point = Point(0, 0)
        set(value) {
            field = value
            positionObservers.forEach { o -> o.onPositionChanged(value) }
        }

    var route: RouteResponseShort? = null
        set(value) {
            field = value
            value?.let { view.onRouteReceived(IDExtractor.roomsInfoListFromIDs(it.route, AreaState.area!!), false) }
        }

    var emergencyRoute: RouteResponseShort? = null
        set(value) {
            field = value
            value?.let { view.onRouteReceived(IDExtractor.roomsInfoListFromIDs(it.route, AreaState.area!!), true) }
        }


    init {
        signalObservers.add(view)
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
//        Log.d("onPositionChanged: " + route?.route?.last()?.serial + " " + cell?.info?.id?.serial)
        if (isSetupFinished() && route?.route?.last()?.serial == cell?.info?.id?.serial) {
            Log.d("onPositionChanged: invalidating route")
            view.onRouteFollowedUntilEnd()
            route = null
        }
    }

    override fun onAreaUpdated(area: AreaViewedFromAUser) {
        view.onAreaUpdated(area)
        position = DistanceHelper.calulateMidPassage(area.rooms
                .filter { (info) -> info.isEntryPoint }.first()
                .passages.filter { p -> p.neighborId == MovementHelper.NEUTRAL_PASSAGE }.first())
    }

    override fun onConnectMessageReceived(connectMessage: String?) {
        Log.d("onConnectMessageReceived: received message")
        connectMessage?.let {
            if (connectMessage == NORMAL_CONNECTION_RESPONSE && isSetupFinished()) {
                cell = AreaState.area?.rooms?.filter { (infoCell) -> infoCell.id.serial == signal.bestNewCandidate.info.id.serial }?.first()
                isSwitching = false
                Log.d("onConnectMessageReceived: cellId" + cell?.info?.id)
            } else if (connectMessage != NORMAL_CONNECTION_RESPONSE && !isSetupFinished()) {
                onAreaUpdated(Unmarshaler.unmarshalArea(it))
                cell = AreaState.area?.rooms?.filter({ (info) -> info.isEntryPoint })?.first()
                Log.d("onConnectMessageReceived: cellId " + cell?.info?.id)
            } else {
                Log.d("onConnectMessageReceived: $connectMessage")
            }
        }
    }

    override fun onAlarmMessageReceived(alarmMessage: String?) {
        Log.d("onAlarmMessageReceived: received ALARM")
        alarmMessage?.let { emergencyRoute = Unmarshaler.unmarshalRouteResponse(it) }
    }

    override fun onRouteMessageReceived(routeMessage: String?) {
        Log.d("onRouteMessageReceived: received ROUTE")
        routeMessage?.let { route = Unmarshaler.unmarshalRouteResponse(it) }
    }

    override fun onSwitchToCellRequested(room: RoomViewedFromAUser) {
        // use for test
//        onConnectMessageReceived(NORMAL_CONNECTION_RESPONSE)
        // use in production
        isSwitching = true
        webSocketHelper.handleSwitch(room.cell)
    }

    override fun onSignalStrengthUpdated(strength: SIGNAL_STRENGTH) {
        signalObservers.forEach({ o -> o.onSignalStrengthUpdated(strength) })
    }

    /**
     * Tells the appropriate websocket to connect, either for the first time or when the connection is lost
     */
    fun askConnection() {
        // use for test
//        if (area == null) onConnectMessageReceived(BufferedReader(InputStreamReader(view.context().resources.openRawResource(R.raw.area))).lines().collect(Collectors.joining("\n")))
//        else onConnectMessageReceived(NORMAL_CONNECTION_RESPONSE)
        // use in production
        webSocketHelper.connectWS.send(if (AreaState.area == null) FIRST_CONNECTION else NORMAL_CONNECTION)
    }

    /**
     * Contacts the websocket and tells it to ask for the specified route
     */
    fun askRoute(departureRoomName: String, arrivalRoomName: String) {
        val depId: Int = AreaState.area!!.rooms.filter { (info) -> info.id.name == departureRoomName }.map { (info) -> info.id.serial }.first()
        val arrId: Int = AreaState.area!!.rooms.filter { (info) -> info.id.name == arrivalRoomName }.map { (info) -> info.id.serial }.first()
        Log.d("askRoute: uri$depId-uri$arrId")
        AreaState.area?.let { webSocketHelper.routeWS.send("uri$depId-uri$arrId") }
    }

    private fun isSetupFinished(): Boolean {
        return AreaState.area != null &&
                cell != null
    }
}