package com.jaus.albertogiunta.teseo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.LinearLayout
import android.widget.TextView
import com.jaus.albertogiunta.teseo.data.*
import com.jaus.albertogiunta.teseo.util.Direction
import com.jaus.albertogiunta.teseo.util.SIGNAL_STRENGTH
import kotlinx.android.synthetic.main.first_open_layout.*
import kotlinx.android.synthetic.main.normal_navigation_layout.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick


interface View : AreaUpdateListener, UserPositionListener, RouteListener, CellUpdateListener, SignalListener {

    /**
     * Get the view context
     */
    fun context(): Context

}

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class MainActivity : View, BaseActivity() {

    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }

        presenter = MainPresenter(this)

        btnFirstConnect.setOnClickListener {
            //            presenter.askConnection()
//            layoutFirst.visibility = android.view.View.GONE
//            layoutSecond.visibility = android.view.View.VISIBLE
//            startActivityForResult(Intent(this, QRReaderActivity::class.java), 1)
        }

        btnLaterConnect.setOnClickListener {
            presenter.askConnection()
//            startActivityForResult(Intent(this, QRReaderActivity::class.java), 1)
        }

        btnRoute.setOnClickListener {
            showRouteDialog()
        }

        btnUp.setOnClickListener {
            detectedMovement(Direction.NORTH)
        }
        btnDown.setOnClickListener {
            detectedMovement(Direction.SOUTH)
        }
        btnLeft.setOnClickListener {
            detectedMovement(Direction.WEST)
        }
        btnRight.setOnClickListener {
            detectedMovement(Direction.EAST)
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
        runOnUiThread {
            drawView.drawArea()
            btnRoute.visibility = android.view.View.VISIBLE
        }
    }

    override fun onPositionChanged(userPosition: Point) {
        runOnUiThread { drawView.drawUserPosition(userPosition) }
    }

    override fun onRouteReceived(route: List<RoomInfo>, isEmergency: Boolean) {
        runOnUiThread {
            drawView.drawRoute(route, isEmergency)
            if (isEmergency) tvEmergencyMode.visibility = android.view.View.VISIBLE
        }
    }

    override fun onRouteFollowedUntilEnd() {
        runOnUiThread {
            toast("You reached your destination!")
            drawView.undrowRoute()
        }
    }

    override fun onSignalStrengthUpdated(strength: SIGNAL_STRENGTH) {
        icSignalStrength.setColorFilter(ContextCompat.getColor(this, strength.tint))
    }

    override fun onCellUpdated(cell: RoomViewedFromAUser) {
        runOnUiThread { tvCurrentRoom.text = cell.info.id.name }
    }

    private fun detectedMovement(direction: Direction) {
        vibrator.vibrate(20)
        val maxStepsAtOnce = 20
        for (i in 0 until maxStepsAtOnce) {
            presenter.onMovementDetected(direction)
        }
    }

    private fun showRouteDialog() {

        val rooms = AreaState.area?.rooms?.map { (info) -> info.id.name }

        var departure: String = ""
        var arrival: String = ""

        alert {
            title = "Choose your route"
            positiveButton("GO!") {
                presenter.askRoute(departure, arrival)
            }
            customView {
                linearLayout {
                    orientation = LinearLayout.VERTICAL
                    var tvDep: TextView? = null
                    var tvArr: TextView? = null

                    button("Choose departure") {
                        onClick {
                            selector("Choose the departure room", rooms!!, { _, i ->
                                departure = rooms[i]
                                tvDep?.text = "Departure:\t\t $departure"
                            })
                        }
                    }
                    tvDep = textView {
                        padding = dip(16)
                    }

                    button("Choose arrival") {
                        onClick {
                            selector("Choose the arrival room", rooms!!, { _, i ->
                                arrival = rooms[i]
                                tvArr?.text = "Arrival:\t\t\t\t\t $arrival"
                            })
                        }
                    }
                    tvArr = textView {
                        padding = dip(16)
                    }
                }
            }
        }.show()
    }
}

