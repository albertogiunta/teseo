package com.jaus.albertogiunta.teseo.kotlin.utils

import com.jaus.albertogiunta.teseo.kotlin.data.Coordinates
import com.jaus.albertogiunta.teseo.kotlin.data.Passage
import com.jaus.albertogiunta.teseo.kotlin.data.Point

object MovementHelper {

    val step = 1
    val NEUTRAL_PASSAGE = -1
    private val padding = 0

    fun isMovementLegit(position: Point, roomVertices: Coordinates, passages: List<Passage>): Boolean {
        if (DistanceHelper.doesPointLieInsideRectangle(position, roomVertices, padding)) {
            return true
        } else {
            passages.filter { (neighborId) -> neighborId != NEUTRAL_PASSAGE }
                    .forEach { (_, startCoordinates, endCoordinates) ->
                        if (DistanceHelper.doesPointLieOnLine(startCoordinates, endCoordinates, position)) {
                            return true
                        }
                    }
            return false
        }
    }
}

object DistanceHelper {

    private val allowance = 1

    fun calculateDistanceBetweenPoints(a: Point, b: Point): Double =
            Math.sqrt((Math.pow((b.x - a.x).toDouble(), 2.0)) + Math.pow((b.y - a.y).toDouble(), 2.0))

    fun doesPointLieInsideRectangle(point: Point, vertices: Coordinates, padding: Int): Boolean {
        return point.y >= vertices.northWest.y + padding && // if under top border
                point.y <= vertices.southWest.y - padding && // if above of bottom border
                point.x <= vertices.northEast.x - padding && // if on the left of right border
                point.x >= vertices.northWest.x + padding       // if on the right of left border
    }

    fun doesPointLieOnLine(a: Point, b: Point, point: Point): Boolean {
        return calculateDistanceBetweenPoints(a, point) +
                calculateDistanceBetweenPoints(point, b) <
                calculateDistanceBetweenPoints(a, b) + allowance
    }

    fun calculateMidPassage(p: Passage): Point =
            calculateMidPoint(p.startCoordinates, p.endCoordinates)

    private fun calculateMidPoint(a: Point, b: Point): Point =
            Point(calculateMidNumber(a.x, b.x), calculateMidNumber(a.y, b.y))

    private fun calculateMidNumber(a: Int, b: Int): Int =
            Math.round((Math.abs(a + b) / 2).toDouble()).toInt()
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