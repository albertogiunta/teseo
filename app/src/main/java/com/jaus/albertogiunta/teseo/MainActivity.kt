package com.jaus.albertogiunta.teseo

import android.os.Bundle
import com.jaus.albertogiunta.teseo.data.Area
import com.jaus.albertogiunta.teseo.data.Point
import kotlinx.android.synthetic.main.activity_main.*
import trikita.log.Log

interface View {

    fun onAreaReceived(area: Area)

    fun onPositionChanged(position: Point)

}

class MainActivity : View, BaseActivity() {

    val presenter: MainPresenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.run()

        btnPrint.setOnClickListener {
            val str = "I want to connect"
            presenter.websocket.send(str)
            Log.d("onCreate: $str")
        }

        btnUp.setOnClickListener { presenter.onMovementDetected(Direction.NORTH) }
        btnDown.setOnClickListener { presenter.onMovementDetected(Direction.SOUTH) }
        btnLeft.setOnClickListener { presenter.onMovementDetected(Direction.WEST) }
        btnRight.setOnClickListener { presenter.onMovementDetected(Direction.EAST) }


    }

    override fun onAreaReceived(area: Area) {
        runOnUiThread { drawView.setNewArea(area) }

    }

    override fun onPositionChanged(position: Point) {
        runOnUiThread { drawView.setUserPosition(position) }
    }

}
