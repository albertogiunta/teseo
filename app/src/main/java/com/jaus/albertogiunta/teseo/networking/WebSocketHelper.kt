package com.jaus.albertogiunta.teseo.networking

import com.jaus.albertogiunta.teseo.CustomWebSocket
import com.jaus.albertogiunta.teseo.WSMessageCallbacks
import com.jaus.albertogiunta.teseo.data.AreaState
import com.jaus.albertogiunta.teseo.data.CellInfo
import com.jaus.albertogiunta.teseo.networking.CHANNEL.*
import com.jaus.albertogiunta.teseo.screens.areaNavigation.MainPresenter
import com.jaus.albertogiunta.teseo.utils.EmulatorUtils
import com.jaus.albertogiunta.teseo.utils.SavedCellUri
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import trikita.log.Log
import java.util.concurrent.TimeUnit
/**
 * Will handle all the needed websockets and will holds the request and response methods for them
 *
 * @property messageCallbacks needed to handle the responses
 */
class WebSocketHelper(private val messageCallbacks: WSMessageCallbacks) {

    lateinit var connectWS: CustomWebSocket
    lateinit var alarmWS: CustomWebSocket
    lateinit var routeWS: CustomWebSocket
    private var isSwitchingAvailable = true

    private var ip: String = if (EmulatorUtils.isOnEmulator()) "ws://10.0.2.2" else "ws://192.168.0.111"
    private var cellUri: String = SavedCellUri.uri
        set(value) {
            field = value
            baseAddress = ip + cellUri
            SavedCellUri.uri = value
            Log.d("Now connecting to $baseAddress")
        }
    private var baseAddress: String = ip + cellUri

    init {
        initWS()
    }

    /**
     * Disconnects from all the current websockets with the current cell and connects to the new one
     *
     * @param cell the new cell to connect to
     */
    fun handleSwitch(cell: CellInfo) {
        if (isSwitchingAvailable) {
            disconnectWS()
            cellUri = ":${cell.port}/${cell.uri}"
            initWS()
        }
    }

    private fun initWS() {
        connectWS = createWebsocketForChannel(CONNECTION)
        routeWS = createWebsocketForChannel(ROUTE)
        alarmWS = createWebsocketForChannel(ALARM)
        if (AreaState.area != null) connectWS.send(MainPresenter.NORMAL_CONNECTION)
    }

    private fun disconnectWS() {
        connectWS.send(MainPresenter.DISCONNECTION)
        alarmWS.send(MainPresenter.DISCONNECTION)
    }

    private fun createWebsocketForChannel(channel: CHANNEL): CustomWebSocket {
        val address = baseAddress + channel.endpoint
        return when (channel) {
            CONNECTION -> BaseWebSocket(address, messageCallbacks::onConnectMessageReceived)
            ROUTE -> BaseWebSocket(address, messageCallbacks::onRouteMessageReceived)
            ALARM -> BaseWebSocket(address, messageCallbacks::onAlarmMessageReceived)
            POSITION_UPDATE -> throw UnsupportedOperationException()
        }
    }
}

class BaseWebSocket(address: String, private val onMessageListener: (text: String?) -> Unit) : CustomWebSocket() {

    private var webSocket: WebSocket

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
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
        t.printStackTrace()
    }

    override fun send(s: String) {
        webSocket.send(s)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        onMessageListener(text)
    }
}

enum class CHANNEL(val endpoint: String) {
    CONNECTION("/connect"),
    ROUTE("/route"),
    ALARM("/alarm"),
    POSITION_UPDATE("/position-update")
}