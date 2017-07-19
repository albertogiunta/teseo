package com.jaus.albertogiunta.teseo.util

import com.jaus.albertogiunta.teseo.data.InfoCell
import com.jaus.albertogiunta.teseo.util.CHANNEL.*
import okhttp3.*
import trikita.log.Log
import java.util.concurrent.TimeUnit

interface Receivers {

    fun onConnectMessageReceived(text: String?)

    fun onAlarmMessageReceived(text: String?)

}

class WebSocketHelper(val receivers: Receivers) {

    lateinit var connectWS: CustomWebSocket
    lateinit var alarmWS: CustomWebSocket
    var baseAddress: String = "ws://10.0.2.2:8080/uri1"

    init {
        initWS()
    }

    fun initWS() {
        connectWS = WebSocketFactory(baseAddress).websocketForChannel(CONNECTION, receivers)
        alarmWS = WebSocketFactory(baseAddress).websocketForChannel(ALARM, receivers)

    }

    fun disconnectWS() {
        connectWS.send("disconnectWS")
        connectWS.send("connect")
        alarmWS.send("connect")
        alarmWS.send("disconnectWS")
    }

    fun handleSwitch(cell: InfoCell) {
        disconnectWS()
        baseAddress = cell.uri // TODO has to be fixed server side
        initWS()
    }
}

class WebSocketFactory(val baseAddress: String) {
    fun websocketForChannel(channel: CHANNEL, receivers: Receivers) : CustomWebSocket {
        val address = baseAddress + channel.endpoint
        when (channel) {
            CONNECTION -> return ConnectionWebSocket(address, receivers::onConnectMessageReceived)
            ALARM -> return AlarmWebSocket(address, receivers::onAlarmMessageReceived)
            ROUTE -> TODO()
            POSITION_UPDATE -> TODO()
        }
    }
}

interface CustomWebSocket {

    fun onMessage(webSocket: WebSocket, text: String)

    fun send(s: String)

    fun run()

}

abstract class BaseWebSocket(val address: String) : CustomWebSocket, WebSocketListener() {

    lateinit var webSocket: WebSocket

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

    override fun run() {

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
        webSocket.close(1000, null)
        System.out.println("CLOSE: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
        t.printStackTrace()
    }

    override fun send(s: String) {
        webSocket.send(s)
    }
}

class ConnectionWebSocket(address: String, val onMessageListener: (text: String?) -> Unit) : BaseWebSocket(address) {

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("onMessage: RICEVUTA AREAAAAAAA $text")
        onMessageListener(text)
    }
}

class AlarmWebSocket(address: String, val onMessageListener: (text: String?) -> Unit) : BaseWebSocket(address) {

    override fun onMessage(webSocket: WebSocket, text: String) {
        onMessageListener(text)
    }
}


enum class CHANNEL(val endpoint: String) {
    CONNECTION("/connect"),
    ROUTE("/route"),
    POSITION_UPDATE("/position-update"),
    ALARM("/alarm")
}