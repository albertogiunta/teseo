package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.AreaViewedFromAUser
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.data.RoomInfo
import com.jaus.albertogiunta.teseo.data.RoomViewedFromAUser
import com.jaus.albertogiunta.teseo.helpers.Direction
import com.jaus.albertogiunta.teseo.helpers.SIGNAL_STRENGTH

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
    fun onRouteFollowedUntilEnd()

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