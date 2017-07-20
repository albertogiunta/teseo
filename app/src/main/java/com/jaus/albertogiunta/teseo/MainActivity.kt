package com.jaus.albertogiunta.teseo

import android.os.Bundle
import com.jaus.albertogiunta.teseo.data.Area
import com.jaus.albertogiunta.teseo.data.InfoCell
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.util.Direction
import kotlinx.android.synthetic.main.activity_main.*

interface View : AreaUpdateListener, UserPositionListener, RouteListener

class MainActivity : View, BaseActivity() {

    val presenter: MainPresenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnConnect.setOnClickListener { presenter.askConnection() }
        btnRoute.setOnClickListener { presenter.askRoute() }

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

    override fun onRouteReceived(route: List<InfoCell>) {
        runOnUiThread { drawView.setRoute(route) }
    }

    override fun onEmergencyRouteReceived(route: List<InfoCell>) {
        runOnUiThread { drawView.setRoute(route) }
    }

    override fun onRouteFollowedUntilEnd() {
        runOnUiThread { drawView.invalidateROute() }
    }
}
