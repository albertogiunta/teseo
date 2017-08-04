package com.jaus.albertogiunta.teseo

import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.util.Direction
import org.junit.Assert.assertEquals
import org.junit.Test

class PositionValidatorTest {

    @Test
    fun PositionValidator_CorrectMovement() {
        assertEquals(Direction.NORTH.operationOnPoint(Point(0, 0)), Point(0, 1))
        assertEquals(Direction.SOUTH.operationOnPoint(Point(0, 0)), Point(0, -1))
        assertEquals(Direction.EAST.operationOnPoint(Point(0, 0)), Point(1, 0))
        assertEquals(Direction.WEST.operationOnPoint(Point(0, 0)), Point(-1, 0))
    }


}