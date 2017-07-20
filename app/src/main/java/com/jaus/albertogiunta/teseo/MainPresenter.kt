package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.*
import com.jaus.albertogiunta.teseo.util.*
import trikita.log.Log

class MainPresenter(val view: View) : AreaUpdateListener, UserMovementListener, UserPositionListener, CellSwitcherListener, Receivers {

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

    var position: Point = Point(0, 0)
        set(value) {
            field = value
            positionObservers.forEach { o -> o.onPositionChanged(value) }
        }

    var route: RouteResponse? = null
        set(value) {
            field = value
            value?.let { view.onRouteReceived(it.route) }
        }

    var emergencyRoute: RouteResponse? = null
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
            if (MovementHelper.isMovementLegit(tempPosition, (cell as CellForCell).infoCell.roomVertices, (cell as CellForCell).passages)) {
                position = tempPosition
            }
        }
    }

    override fun onPositionChanged(userPosition: Point) {
        if (route?.route?.last()?.id == cell?.infoCell?.id) {
            view.onRouteFollowedUntilEnd()
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
                Log.d("onConnectMessageReceived: " + cell?.infoCell?.id)
            }
        }
    }

    override fun onAlarmMessageReceived(text: String?) {
        text?.let { emergencyRoute = Unmarshalers.unmarshalMap(it) }
    }

    override fun onRouteMessageReceived(text: String?) {
        text?.let { route = Unmarshalers.unmarshalMap(it) }
    }

    fun askConnection() {
        val msg = if (area == null) "firstconnection" else "connect"
        webSocketHelper.connectWS.send(msg)
    }

    fun askRoute() {
        Log.d("askRoute: ${area!!.cells.first().infoCell.id}-${area!!.cells.last().infoCell.id}")
        area?.let { webSocketHelper.routeWS.send("${it.cells.first().infoCell.id}-${it.cells.last().infoCell.id}") }
    }

    override fun onSwitchToCellRequested(cell: InfoCell) {
        webSocketHelper.handleSwitch(cell)
    }
}