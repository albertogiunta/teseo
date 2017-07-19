package com.jaus.albertogiunta.teseo

import android.os.Bundle
import com.jaus.albertogiunta.teseo.data.Area
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.util.Direction
import kotlinx.android.synthetic.main.activity_main.*
import trikita.log.Log

interface View : AreaUpdateListener, UserPositionListener

class MainActivity : View, BaseActivity() {

    val presenter: MainPresenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPrint.setOnClickListener {
            val str = "connect"
            presenter.askConnection()
            Log.d("onCreate: $str")
        }

        btnUp.setOnClickListener { presenter.onMovementDetected(Direction.NORTH) }
        btnDown.setOnClickListener { presenter.onMovementDetected(Direction.SOUTH) }
        btnLeft.setOnClickListener { presenter.onMovementDetected(Direction.WEST) }
        btnRight.setOnClickListener { presenter.onMovementDetected(Direction.EAST) }
    }

    override fun onAreaUpdated(area: Area) {
        runOnUiThread { drawView.setNewArea(area) }
    }

    override fun onPositionChanged(userPosition: Point) {
        runOnUiThread { drawView.setUserPosition(userPosition) }
    }

}
