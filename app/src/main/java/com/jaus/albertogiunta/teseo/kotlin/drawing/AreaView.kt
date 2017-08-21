package com.jaus.albertogiunta.teseo.kotlin.drawing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jaus.albertogiunta.teseo.kotlin.data.AreaState
import com.jaus.albertogiunta.teseo.kotlin.data.Point
import com.jaus.albertogiunta.teseo.kotlin.data.RoomInfo

class AreaView : View {

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
        paintWalls = createPaint(Paint.Style.STROKE, 3f, Color.WHITE)
        paintPassages = createPaint(Paint.Style.STROKE, 5f, Color.DKGRAY)
        paintAntennas = createPaint(Paint.Style.STROKE, 5f, Color.WHITE)
        paintUser = createPaint(Paint.Style.FILL, 10f, Color.CYAN)
        paintRoute = createPaint(Paint.Style.FILL, 10f, Color.GREEN)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (shouldDisplayArea) {
            AreaState.area?.rooms?.forEach { cell ->
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

                        if (index == route.size - 2) {
                            val paintAim: Paint = createPaint(Paint.Style.FILL, 20f, Color.WHITE)
                            val paintAim2: Paint = createPaint(Paint.Style.FILL, 20f, Color.BLUE)
                            paintAim.color
                            canvas.drawCircle(stop.antennaPosition.x.toFloat(),
                                    stop.antennaPosition.y.toFloat(),
                                    20f, paintAim)
                            canvas.drawCircle(stop.antennaPosition.x.toFloat(),
                                    stop.antennaPosition.y.toFloat(),
                                    10f, paintAim2)
                        }
                    }
                }
            }

            if (shouldDisplayUser) {
                canvas.drawCircle(position.x.toFloat(),
                        position.y.toFloat(),
                        15f,
                        paintUser)
            }
        }
    }

    fun drawArea() {
        shouldDisplayArea = true
        redraw()
    }

    fun drawUserPosition(position: Point) {
        this.position = position
        shouldDisplayUser = true
        redraw()
    }

    fun drawRoute(route: List<RoomInfo>, isEmergency: Boolean) {
        if (isEmergency || (!isEmergency && !isEmergencyModeOn)) {
            if (isEmergency) paintRoute.color = Color.RED else Color.GREEN
            isEmergencyModeOn = isEmergency
            shouldDisplayRoute = true
            this.route = route
            redraw()
        }
    }

    fun undrowRoute() {
        isEmergencyModeOn = false
        shouldDisplayRoute = false
        redraw()
    }

    private fun createPaint(style: Paint.Style, strokeWidth: Float, color: Int): Paint {
        val p = Paint()
        p.style = style
        p.strokeWidth = strokeWidth
        p.color = color
        return p
    }

    private fun redraw() {
        invalidate()
        requestLayout()
    }
}