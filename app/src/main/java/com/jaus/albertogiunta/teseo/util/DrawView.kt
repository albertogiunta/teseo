package com.jaus.albertogiunta.teseo.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jaus.albertogiunta.teseo.data.Area
import com.jaus.albertogiunta.teseo.data.InfoCell
import com.jaus.albertogiunta.teseo.data.Point

class DrawView : View {

    private val multiplier = 20
    private lateinit var area: Area
    private lateinit var position: Point
    private lateinit var route: List<InfoCell>
    private var shouldDisplayArea = false
    private var shouldDisplayUser = false
    private var shouldDisplayRoute = false
    private var shouldDisplayEmergencyRoute = false
    private var paintWalls: Paint = Paint()
    private var paintPassages: Paint = Paint()
    private var paintAntennas: Paint = Paint()
    private var paintUser: Paint = Paint()
    private var paintRoute: Paint = Paint()

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        paintWalls = createPaint(Paint.Style.STROKE, 3f, Color.BLACK)
        paintPassages = createPaint(Paint.Style.STROKE, 5f, Color.WHITE)
        paintAntennas = createPaint(Paint.Style.FILL, 30f, Color.BLACK)
        paintUser = createPaint(Paint.Style.FILL, 20f, Color.CYAN)
        paintRoute = createPaint(Paint.Style.FILL, 10f, Color.GREEN)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (shouldDisplayRoute || shouldDisplayEmergencyRoute) {
            if (shouldDisplayEmergencyRoute) paintRoute.color = Color.RED else Color.GREEN
            route.forEachIndexed { index, infoCell ->
                if (index < route.size - 1) {
                    val stop: InfoCell = route[index + 1]
                    canvas.drawLine(infoCell.antennaPosition.x.toFloat(),
                            infoCell.antennaPosition.y.toFloat(),
                            stop.antennaPosition.x.toFloat(),
                            stop.antennaPosition.y.toFloat(),
                            paintRoute)
                }
            }
        }

        if (shouldDisplayArea) {
            area.cells.forEach { cell ->
                canvas.drawRect(cell.info.roomVertices.northWest.x.toFloat(),
                        cell.info.roomVertices.northWest.y.toFloat(),
                        cell.info.roomVertices.southEast.x.toFloat(),
                        cell.info.roomVertices.southEast.y.toFloat(),
                        paintWalls)

                cell.passages.forEach { p ->
                    canvas.drawLine(p.startCoordinates.x.toFloat(),
                            p.startCoordinates.y.toFloat(),
                            p.endCoordinates.x.toFloat(),
                            p.endCoordinates.y.toFloat(),
                            paintPassages)
                }

                canvas.drawCircle(cell.info.antennaPosition.x.toFloat(),
                        cell.info.antennaPosition.y.toFloat(),
                        6f,
                        paintAntennas)
            }

            if (shouldDisplayUser) {
                canvas.drawCircle(position.x.toFloat(),
                        position.y.toFloat(),
                        20f,
                        paintUser)
            }
        }
    }

    private fun createPaint(style: Paint.Style, strokeWidth: Float, color: Int): Paint {
        val p: Paint = Paint()
        p.style = style
        p.strokeWidth = strokeWidth
        p.color = color
        return p
    }

    fun setNewArea(newArea: Area) {
        this.area = newArea
        shouldDisplayArea = true
        redraw()
    }

    fun setUserPosition(position: Point) {
        this.position = position
        shouldDisplayUser = true
        redraw()
    }

    fun setRoute(route: List<InfoCell>) {
        if (!shouldDisplayEmergencyRoute) {
            this.route = route
            shouldDisplayRoute = true
            shouldDisplayEmergencyRoute = false
            redraw()
        }
    }

    fun setEmergencyRoute(route: List<InfoCell>) {
        this.route = route
        shouldDisplayRoute = false
        shouldDisplayEmergencyRoute = true
        redraw()
    }

    fun invalidateRoute() {
        shouldDisplayRoute = false
        shouldDisplayEmergencyRoute = false
        redraw()
    }

    private fun redraw() {
        invalidate()
        requestLayout()
    }
}