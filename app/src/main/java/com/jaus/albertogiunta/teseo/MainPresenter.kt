package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.Area
import com.jaus.albertogiunta.teseo.data.CellForCell
import com.jaus.albertogiunta.teseo.data.InfoCell
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.util.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import trikita.log.Log


interface CellSwitch {
    fun switchToCell(cell: InfoCell)
}

class MainPresenter(val view: View) : AreaUpdateListener, UserMovementListener, CellSwitch, Receivers {

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

    init {
//        area = unmarshalArea("")
//        cell = area!!.cells.filter { c -> c.isEntryPoint }.first()
//        view.onAreaUpdated(area!!)

        positionObservers.add(signal)
        positionObservers.add(view)

    }

    private fun unmarshalArea(string: String): Area {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Area> = moshi.adapter(Area::class.java)
//        return jsonAdapter.fromJson(AreaJson.json) as Area
        return jsonAdapter.fromJson(string) as Area
    }

    override fun onMovementDetected(direction: Direction) {

        val tempPosition = direction.operationOnPoint(position)

        cell?.let {
            if (MovementHelper.isMovementLegit(tempPosition, (cell as CellForCell).infoCell.roomVertices, (cell as CellForCell).passages)) {
                position = tempPosition

            }
        }
    }

    override fun onAreaUpdated(area: Area) {
        this.area = area
    }

    override fun onConnectMessageReceived(text: String?) {
        Log.d("onConnectMessageReceived: received message $text")

        text?.let { onAreaUpdated(unmarshalArea(it)) }
    }

    override fun onAlarmMessageReceived(text: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun askConnection() {
        val msg = if (area == null) "firstconnection" else "connect"
        webSocketHelper.connectWS.send(msg)
        Log.d("askConnection: sent message $msg")
    }

    override fun switchToCell(cell: InfoCell) {
        TODO()
    }
}