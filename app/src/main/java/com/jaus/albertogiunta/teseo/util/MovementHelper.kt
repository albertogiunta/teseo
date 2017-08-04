package com.jaus.albertogiunta.teseo.util

import com.jaus.albertogiunta.teseo.data.Coordinates
import com.jaus.albertogiunta.teseo.data.Passage
import com.jaus.albertogiunta.teseo.data.Point

object MovementHelper {

    val padding = 0
    val step = 1

    fun isMovementLegit(position: Point, roomVertices: Coordinates, passages: List<Passage>): Boolean {
        if (DistanceHelper.doesPointLieInsideRectangle(position, roomVertices, padding)) {
            return true
        } else {
            passages.forEach { (_, startCoordinates, endCoordinates) ->
                if (DistanceHelper.doesPointLieOnLine(startCoordinates, endCoordinates, position)) {
                    return true
                }
            }
            return false
        }
    }
}

object DistanceHelper {

    val allowance = 1

    fun calculateDistanceBetweenPoints(a: Point, b: Point): Double {
        return Math.sqrt((Math.pow((b.x - a.x).toDouble(), 2.0)) + Math.pow((b.y - a.y).toDouble(), 2.0))
    }

    fun doesPointLieInsideRectangle(point: Point, vertices: Coordinates, padding: Int): Boolean {
        val a = point.y >= vertices.northWest.y + padding && // if under top border
                point.y <= vertices.southWest.y - padding && // if above of bottom border
                point.x <= vertices.northEast.x - padding &&    // if on the left of right border
                point.x >= vertices.northWest.x + padding       // if on the right of left border
        return a
    }

   fun doesPointLieOnLine(a: Point, b: Point, point: Point): Boolean {
        return DistanceHelper.calculateDistanceBetweenPoints(a, point) + DistanceHelper.calculateDistanceBetweenPoints(point, b) < DistanceHelper.calculateDistanceBetweenPoints(a, b) + allowance
    }
}

enum class Direction {

    NORTH {
        override fun operationOnPoint(originalPoint: Point): Point = Point(originalPoint.x, originalPoint.y - MovementHelper.step)
    },

    SOUTH {
        override fun operationOnPoint(originalPoint: Point): Point = Point(originalPoint.x, originalPoint.y + MovementHelper.step)
    },

    WEST {
        override fun operationOnPoint(originalPoint: Point): Point = Point(originalPoint.x - MovementHelper.step, originalPoint.y)
    },

    EAST {
        override fun operationOnPoint(originalPoint: Point): Point = Point(originalPoint.x + MovementHelper.step, originalPoint.y)
    };

    abstract fun operationOnPoint(originalPoint: Point): Point
}