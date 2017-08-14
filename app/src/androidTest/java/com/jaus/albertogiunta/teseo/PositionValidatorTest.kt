package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.Coordinates
import com.jaus.albertogiunta.teseo.data.Passage
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.helpers.Direction
import com.jaus.albertogiunta.teseo.helpers.DistanceHelper
import com.jaus.albertogiunta.teseo.helpers.MovementHelper
import org.junit.Assert.*
import org.junit.Test

class PositionValidatorTest {

    @Test
    fun PositionValidator_CorrectMovement() {
        assertEquals(Direction.NORTH.operationOnPoint(Point(0, 0)), Point(0, -1))
        assertEquals(Direction.SOUTH.operationOnPoint(Point(0, 0)), Point(0, 1))
        assertEquals(Direction.EAST.operationOnPoint(Point(0, 0)), Point(1, 0))
        assertEquals(Direction.WEST.operationOnPoint(Point(0, 0)), Point(-1, 0))
    }

    @Test
    fun MovementValidator_PointLiesInsideRectangle() {
        assertTrue(DistanceHelper.doesPointLieInsideRectangle(Point(150, 150), Coordinates(Point(100, 100), Point(250, 100), Point(100, 200), Point(250, 200)), 0))
        assertTrue(DistanceHelper.doesPointLieInsideRectangle(Point(100, 150), Coordinates(Point(100, 100), Point(250, 100), Point(100, 200), Point(250, 200)), 0))
        assertFalse(DistanceHelper.doesPointLieInsideRectangle(Point(0, 0), Coordinates(Point(100, 100), Point(250, 100), Point(100, 200), Point(250, 200)), 0))
    }

    @Test
    fun MovementValidator_PointLiesOnLine() {
        assertTrue(DistanceHelper.doesPointLieOnLine(Point(0, 0), Point(10, 0), Point(5, 0)))
        assertTrue(DistanceHelper.doesPointLieOnLine(Point(0, 0), Point(10, 0), Point(0, 0)))
        assertTrue(DistanceHelper.doesPointLieOnLine(Point(0, 0), Point(10, 0), Point(10, 0)))
        assertFalse(DistanceHelper.doesPointLieOnLine(Point(0, 0), Point(10, 0), Point(-1, 0)))
    }

    @Test
    fun MovementValidator_DistanceBetweenPoints() {
        assertEquals(DistanceHelper.calculateDistanceBetweenPoints(Point(0, 0), Point(10, 0)), 10.0, 0.0)
        assertEquals(DistanceHelper.calculateDistanceBetweenPoints(Point(0, 0), Point(0, 10)), 10.0, 0.0)
        assertEquals(DistanceHelper.calculateDistanceBetweenPoints(Point(0, 0), Point(10, 10)), 14.142135623730951, 0.0)
        assertNotEquals(DistanceHelper.calculateDistanceBetweenPoints(Point(0, 0), Point(11, 0)), 10.0, 0.0)
    }

    @Test
    fun MovementValidator_IsMovementLegit() {
        val coordinates: Coordinates = Coordinates(Point(100, 100), Point(250, 100), Point(100, 200), Point(250, 200))
        val passages: List<Passage> = arrayListOf(Passage(MovementHelper.NEUTRAL_PASSAGE, Point(100, 130), Point(100, 200)), Passage(1, Point(100, 130), Point(100, 200)),
                Passage(1, Point(200, 130), Point(200, 200)), Passage(1, Point(100, 130), Point(100, 200)))
        testMovementGeneral(Point(150, 150), coordinates, passages, true, true, true, true)
        testMovementGeneral(Point(100, 100), coordinates, passages, false, true, true, false)
        testMovementGeneral(Point(100, 120), coordinates, passages, true, true, true, false)
        testMovementGeneral(Point(100, 150), coordinates, passages, true, true, true, true)
    }

    fun testMovementGeneral(point: Point, coordinates: Coordinates, passages: List<Passage>, isNorthOk: Boolean, isEastOk: Boolean, isSouthOk: Boolean, isWestOk: Boolean) {
        assertEquals(MovementHelper.isMovementLegit(Direction.NORTH.operationOnPoint(point), coordinates, passages), isNorthOk)
        assertEquals(MovementHelper.isMovementLegit(Direction.EAST.operationOnPoint(point), coordinates, passages), isEastOk)
        assertEquals(MovementHelper.isMovementLegit(Direction.SOUTH.operationOnPoint(point), coordinates, passages), isSouthOk)
        assertEquals(MovementHelper.isMovementLegit(Direction.WEST.operationOnPoint(point), coordinates, passages), isWestOk)
    }

}