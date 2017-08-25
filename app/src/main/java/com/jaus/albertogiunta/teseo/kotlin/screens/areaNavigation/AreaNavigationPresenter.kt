package com.jaus.albertogiunta.teseo.kotlin.screens.areaNavigation

import com.jaus.albertogiunta.teseo.kotlin.*
import com.jaus.albertogiunta.teseo.kotlin.data.*
import com.jaus.albertogiunta.teseo.kotlin.networking.SIGNAL_STRENGTH
import com.jaus.albertogiunta.teseo.kotlin.networking.SignalHelper
import com.jaus.albertogiunta.teseo.kotlin.networking.WebSocketHelper
import com.jaus.albertogiunta.teseo.kotlin.utils.*
import trikita.log.Log

class MainPresenter(private val view: AreaNavigationView) : AreaNavigationPresenter {

    private var isSwitching: Boolean = false

    private var signal: SignalHelper = SignalHelper(this)

    private val webSocketHelper: WebSocketHelper by lazy {
        WebSocketHelper(this)
    }

    private var signalObservers: MutableSet<SignalListener> = mutableSetOf()
    private var positionObservers: MutableSet<UserPositionListener> = mutableSetOf()
    private var areaObservers: MutableSet<AreaUpdateListener> = mutableSetOf()
    private var cellObservers: MutableSet<CellUpdateListener> = mutableSetOf()
    private var sysShutdownObservers: MutableSet<SystemShutdownListener> = mutableSetOf()

    private var cell: RoomViewedFromAUser? = null
        set(value) {
            field = value
            cellObservers.forEach { o ->
                value?.let {
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
            if (value != null && AreaState.area != null) {
                view.onRouteReceived(IDExtractor.roomsInfoListFromIDs(value.route, AreaState.area!!), false)
            }
        }

    private var emergencyRoute: RouteResponseShort? = null
        set(value) {
            field = value
            if (value != null && AreaState.area != null) {
                view.onRouteReceived(IDExtractor.roomsInfoListFromIDs(value.route, AreaState.area!!), true)
            }
        }

    private var isResetting: Boolean = false

    init {
        signalObservers.add(view)
        sysShutdownObservers.add(view)
        areaObservers.add(view)
        areaObservers.add(webSocketHelper)
        cellObservers.add(view)
        cellObservers.add(signal)
        positionObservers.add(this)
        positionObservers.add(signal)
        positionObservers.add(view)
    }

    override fun onStop() {
        webSocketHelper.disconnectWS()
        AreaState.area = null
        cell = null
        view.invalidateRoute(isEmergency = false, showToast = false)
    }

    override fun onResume() {
        if (!isSetupFinished()) {
            signal = SignalHelper(this)
            view.toggleViews(areaOn = false)
            askConnection()
        }
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
            view.invalidateRoute(false, true)
            route = null
        }
    }

    override fun onAreaUpdated(area: AreaViewedFromAUser) {
        cell = AreaState.area?.rooms?.first({ (info) -> info.id.serial == IDExtractor.getSerialFromIP(UriPrefs.firstAddressByQRCode) })
        position = if (cell!!.info.isEntryPoint) DistanceHelper.calculateMidPassage(cell!!.passages.first { (neighborId) -> neighborId == MovementHelper.NEUTRAL_PASSAGE }) else cell!!.info.antennaPosition
        areaObservers.forEach { o -> o.onAreaUpdated(area) }
    }

    override fun onConnectMessageReceived(connectMessage: String?) {
        connectMessage?.let {
            if (connectMessage == MsgFromWebsocket.NORMAL_CONNECTION_RESPONSE && isSetupFinished()) {
                cell = AreaState.area?.rooms?.first { (infoCell) -> infoCell.id.serial == signal.bestNewCandidate.info.id.serial }
                if (isResetting) position = cell!!.info.antennaPosition; isResetting = false
                isSwitching = false
            } else if (connectMessage != MsgFromWebsocket.NORMAL_CONNECTION_RESPONSE && !isSetupFinished()) {
                onAreaUpdated(Unmarshaler.unmarshalArea(it))
            } else {
                Log.d("onConnectMessageReceived: message not processed: $connectMessage")
            }
        }
    }

    override fun onAlarmMessageReceived(alarmMessage: String?) {
        Log.d("onAlarmMessageReceived: $alarmMessage")
        when (alarmMessage) {
            MsgFromWebsocket.END_ALARM -> view.invalidateRoute(true, true)
            MsgFromWebsocket.SYS_SHUTDOWN -> sysShutdownObservers.forEach { o -> o.onShutdownReceived() }
            else -> emergencyRoute = Unmarshaler.unmarshalRouteResponse(alarmMessage!!)
        }
    }

    override fun onRouteMessageReceived(routeMessage: String?) {
        Log.d("onRouteMessageReceived: $routeMessage")
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
        if (isResetting) {
            onSwitchToCellRequested(IDExtractor.roomFromUri(IDExtractor.getSerialFromIP(UriPrefs.firstAddressByQRCode), AreaState.area!!))
        } else {
            webSocketHelper.connectWS.send(if (isSetupFinished()) MsgToWebsocket.NORMAL_CONNECTION else MsgToWebsocket.FIRST_CONNECTION)
        }
    }

    override fun askRoute(departureRoomName: String, arrivalRoomName: String) {
        val depId: Int = AreaState.area!!.rooms.filter { (info) -> info.id.name == departureRoomName }.map { (info) -> info.id.serial }.first()
        val arrId: Int = AreaState.area!!.rooms.filter { (info) -> info.id.name == arrivalRoomName }.map { (info) -> info.id.serial }.first()
        AreaState.area?.let { webSocketHelper.routeWS.send("uri$depId-uri$arrId") }
    }

    override fun startReset() {
        isResetting = true
    }

    private fun isSetupFinished(): Boolean {
        return AreaState.area != null &&
                cell != null
    }
}