package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.*
import com.jaus.albertogiunta.teseo.util.*
import trikita.log.Log

class MainPresenter(val view: View) : AreaUpdateListener, UserMovementListener, UserPositionListener, CellSwitcherListener, Receivers {

    companion object {
        const val FIRST_CONNECTION = "firstconnection"
        const val NORMAL_CONNECTION = "connect"
    }

    var signal: SignalHelper = SignalHelper(this)

    var webSocketHelper = WebSocketHelper(this)

    var area: Area? = null
        set(value) {
            field = value
            value?.let { view.onAreaUpdated(it) }
        }

    var positionObservers: MutableList<UserPositionListener> = mutableListOf()

    var cell: CellForCell? = null
        set(value) {
            field = value
            value?.let { signal.onCellUpdated(it) }
        }

    var position: Point = Point(1, 1)
        set(value) {
            field = value
            positionObservers.forEach { o -> o.onPositionChanged(value) }
        }

    var route: RouteResponseShort? = null
        set(value) {
            field = value
            value?.let { view.onRouteReceived(it.route) }
        }

    var emergencyRoute: RouteResponseShort? = null
        set(value) {
            field = value
            value?.let { view.onEmergencyRouteReceived(it.route) }
        }


    init {
//        area = unmarshalArea("")
//        cell = area!!.cells.filter { c -> c.isEntryPoint }.first()
//        view.onAreaUpdated(area!!)

        positionObservers.add(signal)
        positionObservers.add(view)
        positionObservers.add(this)

    }

    override fun onMovementDetected(direction: Direction) {
        val tempPosition = direction.operationOnPoint(position)
        cell?.let {
            if (MovementHelper.isMovementLegit(tempPosition, (cell as CellForCell).info.roomVertices, (cell as CellForCell).passages)) {
                position = tempPosition
            }
        }
    }

    override fun onPositionChanged(userPosition: Point) {
//        Log.d("onPositionChanged: " + route?.route?.last()?.id + " " + cell?.info?.id)
        if (route?.route?.last()?.id == cell?.info?.id) {
            view.onRouteFollowedUntilEnd()
            route = null
        }
    }

    override fun onAreaUpdated(area: Area) {
        this.area = area
    }

    override fun onConnectMessageReceived(text: String?) {
        Log.d("onConnectMessageReceived: received message")
        text?.let {
            if (text != "ack") {
                onAreaUpdated(Unmarshalers.unmarshalArea(it))
                cell = area?.cells?.filter(CellForCell::isEntryPoint)?.first()
            } else {
                cell = area?.cells?.filter { (infoCell) -> infoCell.id == signal.bestNewCandidate.id }?.first()
                Log.d("onConnectMessageReceived: " + cell?.info?.id)
            }
        }
    }

    override fun onAlarmMessageReceived(text: String?) {
        Log.d("onAlarmMessageReceived: received ALARM")
        text?.let { emergencyRoute = Unmarshalers.unmarshalShort(it) }
    }

    override fun onRouteMessageReceived(text: String?) {
        Log.d("onRouteMessageReceived: received ROUTE")
        text?.let { route = Unmarshalers.unmarshalShort(it) }
    }

    fun askConnection() {
        val msg = if (area == null) FIRST_CONNECTION else NORMAL_CONNECTION
        webSocketHelper.connectWS.send(msg)
    }

    fun askRoute() {
        Log.d("askRoute: ${area?.cells?.first()?.info?.id}-${area?.cells?.last()?.info?.id}")
        area?.let { webSocketHelper.routeWS.send("${it.cells.first().info.id}-${it.cells.last().info.id}") }
    }

    override fun onSwitchToCellRequested(cell: InfoCell) {
        webSocketHelper.handleSwitch(cell)
    }
}