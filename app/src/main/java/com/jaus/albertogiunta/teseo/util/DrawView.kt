package com.jaus.albertogiunta.teseo.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jaus.albertogiunta.teseo.data.AreaViewedFromAUser
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.data.RoomInfo

class DrawView : View {

    private val multiplier = 20
    private lateinit var area: AreaViewedFromAUser
    private lateinit var position: Point
    private lateinit var route: List<RoomInfo>
    private var shouldDisplayArea = false
    private var shouldDisplayUser = false
    private var shouldDisplayRoute = false
    private var isEmergencyModeOn = false
    private var paintWalls: Paint = Paint()
    private var paintPassages: Paint = Paint()
    private var paintAntennas: Paint = Paint()
    private var paintUser: Paint = Paint()
    private var paintRoute: Paint = Paint()

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        paintWalls = createPaint(Paint.Style.STROKE, 3f, Color.BLACK)
        paintPassages = createPaint(Paint.Style.STROKE, 5f, Color.WHITE)
        paintAntennas = createPaint(Paint.Style.STROKE, 5f, Color.BLACK)
        paintUser = createPaint(Paint.Style.FILL, 20f, Color.CYAN)
        paintRoute = createPaint(Paint.Style.FILL, 10f, Color.GREEN)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (shouldDisplayArea) {
            area.rooms.forEach { cell ->
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
                        7f,
                        paintAntennas)
            }

            if (shouldDisplayRoute) {
                route.forEachIndexed { index, infoCell ->
                    if (index < route.size - 1) {
                        val stop: RoomInfo = route[index + 1]
                        canvas.drawLine(infoCell.antennaPosition.x.toFloat(),
                                infoCell.antennaPosition.y.toFloat(),
                                stop.antennaPosition.x.toFloat(),
                                stop.antennaPosition.y.toFloat(),
                                paintRoute)
                    }
                }
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

    fun setNewArea(newArea: AreaViewedFromAUser) {
        this.area = newArea
        shouldDisplayArea = true
        redraw()
    }

    fun setUserPosition(position: Point) {
        this.position = position
        shouldDisplayUser = true
        redraw()
    }

    fun setRoute(route: List<RoomInfo>, isEmergency: Boolean) {
        if (isEmergency || (!isEmergency && !isEmergencyModeOn)) {
            if (isEmergency) paintRoute.color = Color.RED else Color.GREEN
            isEmergencyModeOn = isEmergency
            shouldDisplayRoute = true
            this.route = route
            redraw()
        }
    }

    fun invalidateRoute() {
        isEmergencyModeOn = false
        shouldDisplayRoute = false
        redraw()
    }

    private fun redraw() {
        invalidate()
        requestLayout()
    }
}