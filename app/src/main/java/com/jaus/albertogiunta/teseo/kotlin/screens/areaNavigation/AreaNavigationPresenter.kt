package com.jaus.albertogiunta.teseo.kotlin.screens.areaNavigation

import com.jaus.albertogiunta.teseo.kotlin.*
import com.jaus.albertogiunta.teseo.kotlin.data.*
import com.jaus.albertogiunta.teseo.kotlin.networking.SIGNAL_STRENGTH
import com.jaus.albertogiunta.teseo.kotlin.networking.SignalHelper
import com.jaus.albertogiunta.teseo.kotlin.networking.WebSocketHelper
import com.jaus.albertogiunta.teseo.kotlin.utils.*
import trikita.log.Log

class MainPresenter(private val view: AreaNavigationView) : AreaNavigationPresenter {

    companion object {
        const val FIRST_CONNECTION = "firstConnection"
        const val NORMAL_CONNECTION = "normalConnection"
        const val DISCONNECTION = "disconnect"
        const val NORMAL_CONNECTION_RESPONSE = "ack"
        const val END_ALARM = "endAlarm"
        const val SYS_SHUTDOWN = "sysShutdown"
    }

    private var isSwitching: Boolean = false

    private var signal: SignalHelper = SignalHelper(this)

    private val webSocketHelper: WebSocketHelper by lazy {
        WebSocketHelper(this)
    }

    private var signalObservers: MutableSet<SignalListener> = mutableSetOf()
    private var positionObservers: MutableSet<UserPositionListener> = mutableSetOf()
    private var cellObservers: MutableSet<CellUpdateListener> = mutableSetOf()
    private var sysShutdownObservers: MutableSet<SystemShutdownListener> = mutableSetOf()

    private var cell: RoomViewedFromAUser? = null
        set(value) {
            field = value
            cellObservers.forEach { o ->
                value?.let {
                    Log.d(": $it")
                    o.onCellUpdated(it)
                }
            }
        }

    private var position: Point = Point(0, 0)
        set(value) {
            field = value
            positionObservers.forEach { o -> o.onPositionChanged(value) }
        }

    private var route: RouteResponseShort? = null
        set(value) {
            field = value
            value?.let { view.onRouteReceived(IDExtractor.roomsInfoListFromIDs(it.route, AreaState.area!!), false) }
        }

    private var emergencyRoute: RouteResponseShort? = null
        set(value) {
            field = value
            value?.let { view.onRouteReceived(IDExtractor.roomsInfoListFromIDs(it.route, AreaState.area!!), true) }
        }


    init {
        signalObservers.add(view)
        sysShutdownObservers.add(view)
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
        if (isSetupFinished() && route?.route?.last()?.serial == cell?.info?.id?.serial) {
            view.invalidateRoute(false)
            route = null
        }
    }

    override fun onAreaUpdated(area: AreaViewedFromAUser) {
        view.onAreaUpdated(area)
        position = DistanceHelper.calculateMidPassage(area.rooms.first { (info) -> info.isEntryPoint }
                .passages.first { (neighborId) -> neighborId == MovementHelper.NEUTRAL_PASSAGE })
    }

    override fun onConnectMessageReceived(connectMessage: String?) {
        connectMessage?.let {
            if (connectMessage == NORMAL_CONNECTION_RESPONSE && isSetupFinished()) {
                cell = AreaState.area?.rooms?.first { (infoCell) -> infoCell.id.serial == signal.bestNewCandidate.info.id.serial }
                isSwitching = false
                Log.d("onConnectMessageReceived: cellId" + cell?.info?.id)
            } else if (connectMessage != NORMAL_CONNECTION_RESPONSE && !isSetupFinished()) {
                onAreaUpdated(Unmarshaler.unmarshalArea(it))
                cell = AreaState.area?.rooms?.first({ (info) -> info.isEntryPoint })
                Log.d("onConnectMessageReceived: cellId " + cell?.info?.id)
            } else {
                Log.d("onConnectMessageReceived: $connectMessage")
            }
        }
    }

    override fun onAlarmMessageReceived(alarmMessage: String?) {
        when (alarmMessage) {
            END_ALARM -> view.invalidateRoute(false)
            SYS_SHUTDOWN -> sysShutdownObservers.forEach { o -> o.onShutdownReceived() }
            else -> emergencyRoute = alarmMessage?.let { Unmarshaler.unmarshalRouteResponse(it) }
        }
    }

    override fun onRouteMessageReceived(routeMessage: String?) {
        routeMessage?.let { route = Unmarshaler.unmarshalRouteResponse(it) }
    }

    override fun onSwitchToCellRequested(room: RoomViewedFromAUser) {
        isSwitching = true
        webSocketHelper.handleSwitch(room.cell)
    }

    override fun onSignalStrengthUpdated(strength: SIGNAL_STRENGTH) {
        signalObservers.forEach({ o -> o.onSignalStrengthUpdated(strength) })
    }


    override fun askConnection() {
        webSocketHelper.connectWS.send(if (isSetupFinished()) NORMAL_CONNECTION else FIRST_CONNECTION)
    }


    override fun askRoute(departureRoomName: String, arrivalRoomName: String) {
        val depId: Int = AreaState.area!!.rooms.filter { (info) -> info.id.name == departureRoomName }.map { (info) -> info.id.serial }.first()
        val arrId: Int = AreaState.area!!.rooms.filter { (info) -> info.id.name == arrivalRoomName }.map { (info) -> info.id.serial }.first()
        AreaState.area?.let { webSocketHelper.routeWS.send("uri$depId-uri$arrId") }
    }

    private fun isSetupFinished(): Boolean {
        return AreaState.area != null &&
                cell != null
    }
}