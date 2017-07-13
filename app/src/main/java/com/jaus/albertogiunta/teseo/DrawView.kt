package com.jaus.albertogiunta.teseo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

class DrawView : View {

    lateinit var area: Area
    var paintShape: Paint = Paint()
    var paintShapeWhite: Paint = Paint()
    var paintShapePoint: Paint = Paint()

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        paintShape.style = Paint.Style.STROKE
        paintShape.color = Color.BLACK
        paintShape.strokeWidth = 3f
        paintShapeWhite.style = Paint.Style.STROKE
        paintShapeWhite.color = Color.WHITE
        paintShapeWhite.strokeWidth = 5f
        paintShapePoint.style = Paint.Style.FILL
        paintShapePoint.color = Color.BLACK
        paintShapePoint.strokeWidth = 10f

        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Area> = moshi.adapter(Area::class.java)
        area = jsonAdapter.fromJson(AreaObj.json) as Area
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        area.cells.forEach { cell ->
            canvas.drawRect(cell.infoCell.roomVertices.northWest.x.toFloat() * 20,
                    cell.infoCell.roomVertices.northWest.y.toFloat() * 20,
                    cell.infoCell.roomVertices.southEast.x.toFloat() * 20,
                    cell.infoCell.roomVertices.southEast.y.toFloat() * 20,
                    paintShape)

            cell.passages.forEach { p ->
                canvas.drawLine(p.startCoordinates.x.toFloat() * 20,
                        p.startCoordinates.y.toFloat() * 20,
                        p.endCoordinates.x.toFloat() * 20,
                        p.endCoordinates.y.toFloat() * 20,
                        paintShapeWhite)
            }

            canvas.drawPoint(cell.infoCell.antennaPosition.x.toFloat() * 20,
                    cell.infoCell.antennaPosition.y.toFloat() * 20,
                    paintShapePoint)
        }
    }
}