package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.Area
import com.jaus.albertogiunta.teseo.data.AreaJson
import com.jaus.albertogiunta.teseo.data.Point
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.*
import java.util.concurrent.TimeUnit


class MainPresenter(val view: View) : WebSocketListener(), OnUserPositionChangedListener {

    var area: Area

    init {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Area> = moshi.adapter(Area::class.java)
        area = jsonAdapter.fromJson(AreaJson.json) as Area
    }

    var position: Point = Point(0, 0)

    var address = "ws://10.0.2.2:8080/connect"

    lateinit var websocket: WebSocket


    fun run() {
        val client = OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build()

        val request = Request.Builder()
                .url(address)
                .build()

        websocket = client.newWebSocket(request, this)

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
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
        view.onAreaReceived(this.area)
    }

    override fun onMovementDetected(direction: Direction) {
        position = direction.operationOnPoint(position)
        view.onPositionChanged(position)
    }
}

interface OnUserPositionChangedListener {

    fun onMovementDetected(direction: Direction)

}

enum class Direction {
    NORTH {
        override fun operationOnPoint(originalPoint: Point): Point = Point(originalPoint.x, originalPoint.y + 1)
    },

    SOUTH {
        override fun operationOnPoint(originalPoint: Point): Point = Point(originalPoint.x, originalPoint.y - 1)
    },

    WEST {
        override fun operationOnPoint(originalPoint: Point): Point = Point(originalPoint.x - 1, originalPoint.y)
    },

    EAST {
        override fun operationOnPoint(originalPoint: Point): Point = Point(originalPoint.x + 1, originalPoint.y)
    };

    abstract fun operationOnPoint(originalPoint: Point): Point
}