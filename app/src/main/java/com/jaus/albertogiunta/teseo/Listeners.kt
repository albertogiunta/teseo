package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.AreaViewedFromAUser
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.data.RoomInfo
import com.jaus.albertogiunta.teseo.data.RoomViewedFromAUser
import com.jaus.albertogiunta.teseo.util.Direction

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

    fun onRouteReceived(route: List<RoomInfo>)

    fun onEmergencyRouteReceived(route: List<RoomInfo>)

    fun onRouteFollowedUntilEnd()

}

interface CellSwitcherListener {

    fun onSwitchToCellRequested(room: RoomViewedFromAUser)

}