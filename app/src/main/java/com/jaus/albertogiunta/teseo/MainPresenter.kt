package com.jaus.albertogiunta.teseo

import okhttp3.*
import java.util.concurrent.TimeUnit


class MainPresenter : WebSocketListener() {

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
    }
}