package com.jaus.albertogiunta.teseo.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jaus.albertogiunta.teseo.data.Area
import com.jaus.albertogiunta.teseo.data.Point

class DrawView : View {

    private lateinit var area: Area
    private lateinit var position: Point
    private var shouldDisplayArea = false
    private var shouldDisplayUser = false
    private var paintWalls: Paint = Paint()
    private var paintPassages: Paint = Paint()
    private var paintAntennas: Paint = Paint()
    private var paintUser: Paint = Paint()

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        paintWalls = createPaint(Paint.Style.STROKE, 3f, Color.BLACK)
        paintPassages = createPaint(Paint.Style.STROKE, 5f, Color.WHITE)
        paintAntennas = createPaint(Paint.Style.FILL, 10f, Color.BLACK)
        paintUser = createPaint(Paint.Style.FILL, 10f, Color.RED)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (shouldDisplayArea) {
            area.cells.forEach { cell ->
                canvas.drawRect(cell.infoCell.roomVertices.northWest.x.toFloat() * 20,
                        cell.infoCell.roomVertices.northWest.y.toFloat() * 20,
                        cell.infoCell.roomVertices.southEast.x.toFloat() * 20,
                        cell.infoCell.roomVertices.southEast.y.toFloat() * 20,
                        paintWalls)

                cell.passages.forEach { p ->
                    canvas.drawLine(p.startCoordinates.x.toFloat() * 20,
                            p.startCoordinates.y.toFloat() * 20,
                            p.endCoordinates.x.toFloat() * 20,
                            p.endCoordinates.y.toFloat() * 20,
                            paintPassages)
                }

                canvas.drawCircle(cell.infoCell.antennaPosition.x.toFloat() * 20,
                        cell.infoCell.antennaPosition.y.toFloat() * 20,
                        6f,
                        paintAntennas)
            }

            if (shouldDisplayUser) {
                canvas.drawCircle(position.x.toFloat() * 20,
                        position.y.toFloat() * 20,
                        6f,
                        paintUser)
            }
        }
    }

//  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//    // Defines the extra padding for the shape name text
//    var textPadding = 10
//    var contentWidth = 400
//
//    // Resolve the width based on our minimum and the measure spec
//    var minw = contentWidth + getPaddingLeft() + getPaddingRight()
//    var w = resolveSizeAndState(minw, widthMeasureSpec, 0)
//
//    // Ask for a height that would let the view get as big as it can
//    var minh = 200 + getPaddingBottom() + getPaddingTop()
//    var h = resolveSizeAndState(minh, heightMeasureSpec, 0)
//
//    // Calling this method determines the measured width and height
//    // Retrieve with getMeasuredWidth or getMeasuredHeight methods later
//    setMeasuredDimension(w, h)
//  }


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
        invalidate()
        requestLayout()
    }

    fun setUserPosition(position: Point) {
        this.position = position
        shouldDisplayUser = true
        invalidate()
        requestLayout()
    }
}