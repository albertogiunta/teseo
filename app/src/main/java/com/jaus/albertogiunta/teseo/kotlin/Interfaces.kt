package com.jaus.albertogiunta.teseo.kotlin

import android.content.Context
import com.jaus.albertogiunta.teseo.kotlin.data.AreaViewedFromAUser
import com.jaus.albertogiunta.teseo.kotlin.data.Point
import com.jaus.albertogiunta.teseo.kotlin.data.RoomInfo
import com.jaus.albertogiunta.teseo.kotlin.data.RoomViewedFromAUser
import com.jaus.albertogiunta.teseo.kotlin.networking.SIGNAL_STRENGTH
import com.jaus.albertogiunta.teseo.kotlin.utils.Direction
import okhttp3.WebSocketListener

interface AreaUpdateListener {

    /**
     * Callback for when an area is received
     *
     * @param area
     */
    fun onAreaUpdated(area: AreaViewedFromAUser)

}

interface CellUpdateListener {

    /**
     * Callback for when a cell is received
     *
     * @param cell
     */
    fun onCellUpdated(cell: RoomViewedFromAUser)

}

interface UserMovementListener {

    /**
     * Callback for when a movement is detected
     *
     * @param direction
     */
    fun onMovementDetected(direction: Direction)

}

interface UserPositionListener {

    /**
     * Callback for when a position is actually changed
     *
     * @param userPosition
     */
    fun onPositionChanged(userPosition: Point)

}

interface RouteListener {

    /**
     * Callback for when a route is received
     *
     * @param route
     * @param isEmergency
     */
    fun onRouteReceived(route: List<RoomInfo>, isEmergency: Boolean)

    /**
     * Callback for when a cell is received
     */
    fun invalidateRoute(isEmergency: Boolean, showToast: Boolean)

}

interface SystemShutdownListener {

    /**
     * Callback for when the system shuts down and the client can't connect to its host anymore
     */
    fun onShutdownReceived()

}

interface SignalListener {

    /**
     * Callback for when the strenght of your own room's signal is updated
     *
     * @param strength
     */
    fun onSignalStrengthUpdated(strength: SIGNAL_STRENGTH)

}

interface CellSwitchingListener {

    /**
     * Callback for when a user got too far from his room's signal and asks to connect to next closest one
     *
     * @param room to connect to
     */
    fun onSwitchToCellRequested(room: RoomViewedFromAUser)
}

interface SignalAndCellSwitchingListener : SignalListener, CellSwitchingListener

/**
 * Callback functions that will handle the different kind of responses received from each kind of websocket
 */
interface WSMessageCallbacks {

    /**
     * Message received after a request for contact is fired.
     *
     * @param connectMessage can be different depending on the request message
     *                          (i.e. simple "ack" or the entire "area")
     */
    fun onConnectMessageReceived(connectMessage: String?)

    /**
     * Received without it being requested (push notification kind of thing)
     *
     * @param alarmMessage is a list of RoomIDs
     */
    fun onAlarmMessageReceived(alarmMessage: String?)

    /**
     * Received after a route request is fired by the user
     *
     * @param routeMessage is a list of @see RoomID
     */
    fun onRouteMessageReceived(routeMessage: String?)

}

/**
 * To be implemented by every presenter, its methods provide a 1:1 mapping with the ones in the activity, in order to correctly handle lifecycle events
 */
interface BasePresenter {

    /**
     * Call when onStop on the view occurs
     */
    fun onStop()

    /**
     * Call when onStop on the view occurs
     */
    fun onResume()
}

interface AreaNavigationView :
        AreaUpdateListener,
        UserPositionListener,
        RouteListener,
        CellUpdateListener,
        SignalListener,
        SystemShutdownListener {

    /**
     * Get the view context
     */
    fun context(): Context

    /**
     * Shows the view instead of the setup activity or not
     */
    fun toggleViews(areaOn: Boolean)
}

interface AreaNavigationPresenter :
        BasePresenter,
        AreaUpdateListener,
        UserMovementListener,
        UserPositionListener,
        SignalAndCellSwitchingListener,
        WSMessageCallbacks {

    /**
     * Tells the appropriate websocket to connect, either for the first time or when the connection is lost
     */
    fun askConnection()

    /**
     * Contacts the websocket and tells it to ask for the specified route
     */
    fun askRoute(departureRoomName: String, arrivalRoomName: String)

    /**
     * Resets the cell value to go through the initial process again
     */
    fun startReset()

}

/**
 * Additional methods that handle the requests and responses to and from the server
 */
private interface TeseoWebSocket {

    /**
     * Sends a message using the websocket opened on a specific channel
     *
     * @param s the message to be sent
     */
    fun send(s: String)

    /**
     * Closes a specific websocket
     */
    fun close()

}

/**
 * A websocket with additional methods that handle the requests and responses to and from the server
 */
abstract class CustomWebSocket : TeseoWebSocket, WebSocketListener()