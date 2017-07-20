package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.Area
import com.jaus.albertogiunta.teseo.data.CellForCell
import com.jaus.albertogiunta.teseo.data.InfoCell
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.util.Direction

interface AreaUpdateListener {

    fun onAreaUpdated(area: Area)

}

interface CellUpdateListener {

    fun onCellUpdated(cell: CellForCell)

}

interface UserMovementListener {

    fun onMovementDetected(direction: Direction)

}

interface UserPositionListener {

    fun onPositionChanged(userPosition: Point)

}

interface RouteListener {

    fun onRouteReceived(route: List<InfoCell>)

    fun onEmergencyRouteReceived(route: List<InfoCell>)

    fun onRouteFollowedUntilEnd()

}

interface CellSwitcherListener {
    fun onSwitchToCellRequested(cell: InfoCell)
}