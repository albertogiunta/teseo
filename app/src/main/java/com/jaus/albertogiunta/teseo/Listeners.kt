package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.Area
import com.jaus.albertogiunta.teseo.data.Cell
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.util.Direction

interface AreaUpdateListener {

    fun onAreaUpdated(area: Area)

}

interface CellUpdateListener {

    fun onCellUpdated(cell: Cell)

}

interface UserMovementListener {

    fun onMovementDetected(direction: Direction)

}

interface UserPositionListener {

    fun onPositionChanged(userPosition: Point)

}