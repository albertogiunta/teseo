package com.jaus.albertogiunta.teseo.networking

import com.jaus.albertogiunta.teseo.WSMessageCallbacks
import com.jaus.albertogiunta.teseo.data.AreaState
import com.jaus.albertogiunta.teseo.data.CellInfo
import com.jaus.albertogiunta.teseo.helpers.SavedCellUri
import com.jaus.albertogiunta.teseo.networking.CHANNEL.*
import com.jaus.albertogiunta.teseo.screens.areaNavigation.MainPresenter
import com.jaus.albertogiunta.teseo.util.EmulatorUtils
import okhttp3.*
import trikita.log.Log
import java.util.concurrent.TimeUnit
/**
 * Will handle all the needed websockets and will holds the request and response methods for them
 *
 * @property messageCallbacks needed to handle the responses
 */
class WebSocketHelper(val messageCallbacks: WSMessageCallbacks) {

    lateinit var connectWS: CustomWebSocket
    lateinit var alarmWS: CustomWebSocket
    lateinit var routeWS: CustomWebSocket
    private var isSwitchingAvailable = true

    private var ip: String = if (EmulatorUtils.isOnEmulator()) "ws://10.0.2.2" else "ws://192.168.0.111"
    //    private var cellUri: String = ":8081/uri1"
    private var cellUri: String = SavedCellUri.uri // use this when using camera qr code
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

    private fun initWS() {
        connectWS = WebSocketFactory(baseAddress).websocketForChannel(CONNECTION, messageCallbacks)
        routeWS = WebSocketFactory(baseAddress).websocketForChannel(ROUTE, messageCallbacks)
        alarmWS = WebSocketFactory(baseAddress).websocketForChannel(ALARM, messageCallbacks)
        if (AreaState.area != null) connectWS.send(MainPresenter.NORMAL_CONNECTION)
    }

    private fun disconnectWS() {
        connectWS.send(MainPresenter.DISCONNECTION)
        alarmWS.send(MainPresenter.DISCONNECTION)
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
}

/**
 * Creates different websockets depending on the parameters passed to it
 *
 * @property baseAddress needed in order to create all the different websockets
 */
class WebSocketFactory(val baseAddress: String) {
    /**
     * Creates a different kind of websocket depending on the channel provided
     *
     * @param channel the channel that discriminates which ws will be created
     * @param messageCallbacks the callback function that will handle the response to a request
     * @return a custom ws that will handle requests and responses
     */
    fun websocketForChannel(channel: CHANNEL, messageCallbacks: WSMessageCallbacks): CustomWebSocket {
        val address = baseAddress + channel.endpoint
        when (channel) {
            CONNECTION -> return BaseWebSocket(address, messageCallbacks::onConnectMessageReceived)
            ROUTE -> return BaseWebSocket(address, messageCallbacks::onRouteMessageReceived)
            ALARM -> return BaseWebSocket(address, messageCallbacks::onAlarmMessageReceived)
            POSITION_UPDATE -> throw UnsupportedOperationException()
        }
    }
}

/**
 * A websocket with additional methods that handle the requests and responses to and from the server
 */
interface CustomWebSocket {

    /**
     * Sends a message using the websocket opened on a specific channel
     *
     * @param s the message to be sent
     */
    fun send(s: String)

    /**
     * Handles responses or more generic messages received from a websocket
     *
     * @param webSocket the websocket instance, useful to handle the sender and other kind of parameters
     * @param text the text sent from the server
     */
    fun onMessage(webSocket: WebSocket, text: String)

}

class BaseWebSocket(address: String, val onMessageListener: (text: String?) -> Unit) : CustomWebSocket, WebSocketListener() {

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