package com.jaus.albertogiunta.teseo.util

import com.jaus.albertogiunta.teseo.data.Coordinates
import com.jaus.albertogiunta.teseo.data.Passage
import com.jaus.albertogiunta.teseo.data.Point

object MovementHelper {

    fun isMovementLegit(position: Point, roomVertices: Coordinates, passages: List<Passage>): Boolean {
        if ((position.y <= roomVertices.northWest.y &&
                position.y >= roomVertices.southWest.y &&
                position.x <= roomVertices.northEast.x &&
                position.x >= roomVertices.northWest.x)) {
            return true
        } else {
            passages.forEach { (_, startCoordinates, endCoordinates) ->
                if (doesPointLieOnLine(startCoordinates, endCoordinates, position)) {
                    return true
                }
            }
            return false
        }
    }

    private fun doesPointLieOnLine(a: Point, b: Point, point: Point): Boolean {
        val allowance = 1
        return DistanceHelper.calculateDistanceBetweenPoints(a, point) + DistanceHelper.calculateDistanceBetweenPoints(point, b) < DistanceHelper.calculateDistanceBetweenPoints(a, b) + allowance
    }
}

object DistanceHelper {
    fun calculateDistanceBetweenPoints(a: Point, b: Point): Double {
        return Math.sqrt((Math.pow((b.x - a.x).toDouble(), 2.0)) + Math.pow((b.y - a.y).toDouble(), 2.0))
    }
}

enum class Direction {

    NORTH {
        override fun operationOnPoint(originalPoint: Point): Point = Point(originalPoint.x, originalPoint.y + 1)
    },

    SOUTH {
        override fun operationOnPoint(originalPoint: Point): Point = Point(originalPoint.x, originalPoint.y - 1)
    },

    WEST {
        override fun operationOnPoint(originalPoint: Point): Point = Point(originalPoint.x - 1, originalPoint.y)
    },

    EAST {
        override fun operationOnPoint(originalPoint: Point): Point = Point(originalPoint.x + 1, originalPoint.y)
    };

    abstract fun operationOnPoint(originalPoint: Point): Point
}