package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.Area
import com.jaus.albertogiunta.teseo.data.AreaJson
import com.jaus.albertogiunta.teseo.data.Cell
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.util.Direction
import com.jaus.albertogiunta.teseo.util.MovementHelper
import com.jaus.albertogiunta.teseo.util.SignalHelper
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.*
import java.util.concurrent.TimeUnit


class MainPresenter(val view: View) : WebSocketListener(), UserMovementListener {

    var signal: SignalHelper = SignalHelper()

    var area: Area? = null
        set(value) {
            field = value
            value?.let { view.onAreaUpdated(it) }
        }

    var positionObservers: MutableList<UserPositionListener> = mutableListOf()

    var cell: Cell? = null
        set(value) {
            field = value
            value?.let { signal.onCellUpdated(it) }
        }

    var position: Point = Point(0, 0)
        set(value) {
            field = value
            positionObservers.forEach { o -> o.onPositionChanged(value) }
        }


    // TODO sposta in file diverso e fa logica per diverse weboskcet
    var address = "ws://10.0.2.2:8080/connect"
    lateinit var webSocket: WebSocket

    init {
        area = unmarshalArea()
        cell = area!!.cells.filter { c -> c.isEntryPoint }.first()
        view.onAreaUpdated(area!!)

        positionObservers.add(signal)
        positionObservers.add(view)

    }

    private fun unmarshalArea(): Area {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Area> = moshi.adapter(Area::class.java)
        return jsonAdapter.fromJson(AreaJson.json) as Area
    }

    override fun onMovementDetected(direction: Direction) {

        val tempPosition = direction.operationOnPoint(position)

        cell?.let {
            if (MovementHelper.isMovementLegit(tempPosition, (cell as Cell).infoCell.roomVertices, (cell as Cell).passages)) {
                position = tempPosition

            }
        }
    }

    fun sendMessageViaWS(s: String) {
        webSocket.send(s)
    }

    /////////////// WEBSOCKET ///////////////

    fun run() {
        val client = OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build()
        val request = Request.Builder()
                .url(address)
                .build()
        webSocket = client.newWebSocket(request, this)
        client.dispatcher().executorService().shutdown()
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
        webSocket.close(1000, null)
        System.out.println("CLOSE: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
        t.printStackTrace()
    }

    override fun onMessage(webSocket: WebSocket, text: String?) {
        System.out.println("MESSAGE: " + text)
//        view.onAreaUpdated(area!!)
    }
}