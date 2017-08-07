package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.AreaViewedFromAUser
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.data.RoomInfo
import com.jaus.albertogiunta.teseo.data.RoomViewedFromAUser
import com.jaus.albertogiunta.teseo.util.Direction
import com.jaus.albertogiunta.teseo.util.SIGNAL_STRENGTH

interface AreaUpdateListener {

    fun onAreaUpdated(area: AreaViewedFromAUser)

}

interface CellUpdateListener {

    fun onCellUpdated(cell: RoomViewedFromAUser)

}

interface UserMovementListener {

    fun onMovementDetected(direction: Direction)

}

interface UserPositionListener {

    fun onPositionChanged(userPosition: Point)

}

interface RouteListener {

    fun onRouteReceived(route: List<RoomInfo>, isEmergency: Boolean)

    fun onRouteFollowedUntilEnd()

}

interface SignalListener {

    fun onSwitchToCellRequested(room: RoomViewedFromAUser)

    fun onSignalStrengthUpdated(strength: SIGNAL_STRENGTH)

}