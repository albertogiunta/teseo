package com.jaus.albertogiunta.teseo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.jaus.albertogiunta.teseo.data.AreaViewedFromAUser
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.data.RoomInfo
import com.jaus.albertogiunta.teseo.util.Direction
import kotlinx.android.synthetic.main.activity_main.*

interface View : AreaUpdateListener, UserPositionListener, RouteListener {
    fun context(): Context
}

class MainActivity : View, BaseActivity() {

    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }

        presenter = MainPresenter(this)

        btnConnect.setOnClickListener { presenter.askConnection() }
        btnQRCode.setOnClickListener { startActivityForResult(Intent(this, QRReaderActivity::class.java), 1) }
        btnRoute.setOnClickListener { presenter.askRoute() }

        val maxStepsAtOnce = 20
        btnUp.setOnClickListener {
            for (i in 0 until maxStepsAtOnce) {
                presenter.onMovementDetected(Direction.NORTH)
            }
        }
        btnDown.setOnClickListener {
            for (i in 0 until maxStepsAtOnce) {
                presenter.onMovementDetected(Direction.SOUTH)
            }
        }
        btnLeft.setOnClickListener {
            for (i in 0 until maxStepsAtOnce) {
                presenter.onMovementDetected(Direction.WEST)
            }
        }
        btnRight.setOnClickListener {
            for (i in 0 until maxStepsAtOnce) {
                presenter.onMovementDetected(Direction.EAST)
            }
        }
    }

    override fun context(): Context {
        return this@MainActivity
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.askConnection()
    }

    override fun onAreaUpdated(area: AreaViewedFromAUser) {
        runOnUiThread { drawView.setNewArea(area) }
    }

    override fun onPositionChanged(userPosition: Point) {
        runOnUiThread { drawView.setUserPosition(userPosition) }
    }

    override fun onRouteReceived(route: List<RoomInfo>) {
        runOnUiThread { drawView.setRoute(route) }
    }

    override fun onEmergencyRouteReceived(route: List<RoomInfo>) {
        runOnUiThread { drawView.setEmergencyRoute(route) }
    }

    override fun onRouteFollowedUntilEnd() {
        this.toast("You reached your destination!")
        runOnUiThread { drawView.invalidateRoute() }
    }
}

