package com.jaus.albertogiunta.teseo.util

import com.jaus.albertogiunta.teseo.data.CellInfo
import com.jaus.albertogiunta.teseo.util.CHANNEL.*
import okhttp3.*
import trikita.log.Log
import java.util.concurrent.TimeUnit

interface Receivers {

    fun onConnectMessageReceived(text: String?)

    fun onAlarmMessageReceived(text: String?)

    fun onRouteMessageReceived(text: String?)

}

class WebSocketHelper(val receivers: Receivers) {

    lateinit var connectWS: CustomWebSocket
    lateinit var alarmWS: CustomWebSocket
    lateinit var routeWS: CustomWebSocket
    var isSwitchingAvailable = true

    //    var ip: String = "ws://10.0.2.2" // uncomment when using emulator
    var ip: String = "ws://192.168.1.107"
    var cellUri: String = ":8081/uri1"
//    var cellUri: String = SavedCellUri.uri // uncomment when using camera qrcode
        set(value) {
            field = value
            baseAddress = ip + cellUri
            SavedCellUri.uri = value
            Log.d("Now connecting to $baseAddress")
        }
    var baseAddress: String = ip + cellUri

    init {
        initWS()
    }

    fun initWS() {
        connectWS = WebSocketFactory(baseAddress).websocketForChannel(CONNECTION, receivers)
        routeWS = WebSocketFactory(baseAddress).websocketForChannel(ROUTE, receivers)
        alarmWS = WebSocketFactory(baseAddress).websocketForChannel(ALARM, receivers)
        connectWS.send("connect")
    }

    fun disconnectWS() {
        connectWS.send("disconnect")
        alarmWS.send("disconnect")
    }

    fun handleSwitch(cell: CellInfo) {
        if (isSwitchingAvailable) {
            disconnectWS()
            cellUri = ":${cell.port}/${cell.uri}"
            initWS()
        }
    }
}

class WebSocketFactory(val baseAddress: String) {
    fun websocketForChannel(channel: CHANNEL, receivers: Receivers): CustomWebSocket {
        val address = baseAddress + channel.endpoint
        when (channel) {
            CONNECTION -> return BaseWebSocket(address, receivers::onConnectMessageReceived)
            ROUTE -> return BaseWebSocket(address, receivers::onRouteMessageReceived)
            ALARM -> return BaseWebSocket(address, receivers::onAlarmMessageReceived)
            POSITION_UPDATE -> TODO()
        }
    }
}

interface CustomWebSocket {

    fun send(s: String)

    fun onMessage(webSocket: WebSocket, text: String)

}

class BaseWebSocket(address: String, val onMessageListener: (text: String?) -> Unit) : CustomWebSocket, WebSocketListener() {

    var webSocket: WebSocket

    init {
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
        Log.d("CLOSE: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
        t.printStackTrace()
    }

    override fun send(s: String) {
        webSocket.send(s)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("onMessage: received message")
        onMessageListener(text)
    }
}

enum class CHANNEL(val endpoint: String) {
    CONNECTION("/connect"),
    ROUTE("/route"),
    ALARM("/alarm"),
    POSITION_UPDATE("/position-update")
}