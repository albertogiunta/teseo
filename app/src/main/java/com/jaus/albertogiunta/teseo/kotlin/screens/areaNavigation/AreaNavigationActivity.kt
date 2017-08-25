package com.jaus.albertogiunta.teseo.kotlin.screens.areaNavigation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.jaus.albertogiunta.teseo.R
import com.jaus.albertogiunta.teseo.kotlin.AreaNavigationPresenter
import com.jaus.albertogiunta.teseo.kotlin.AreaNavigationView
import com.jaus.albertogiunta.teseo.kotlin.data.*
import com.jaus.albertogiunta.teseo.kotlin.networking.SIGNAL_STRENGTH
import com.jaus.albertogiunta.teseo.kotlin.screens.BaseActivity
import com.jaus.albertogiunta.teseo.kotlin.screens.initialSetup.InitialSetupActivity
import com.jaus.albertogiunta.teseo.kotlin.utils.Direction
import com.jaus.albertogiunta.teseo.kotlin.utils.UriPrefs
import kotlinx.android.synthetic.main.activity_area_navigation.*
import kotlinx.android.synthetic.main.layout_launch.*
import kotlinx.android.synthetic.main.layout_normal_navigation.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import trikita.log.Log

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class MainActivity : AreaNavigationView, BaseActivity() {

    private lateinit var presenter: AreaNavigationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area_navigation)

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }

        btnFirstConnect.setOnClickListener {
            startActivityForResult(Intent(this, InitialSetupActivity::class.java), 1)
        }

        etIPAddress.setText(UriPrefs.firstAddressByQRCode, TextView.BufferType.EDITABLE)
        btnSaveIPAddress.setOnClickListener {
            UriPrefs.firstAddressByQRCode = etIPAddress.text.toString()
            onActivityResult(0, 0, null)
        }

        btnLaterConnect.setOnClickListener {
            presenter.onStop()
            toggleViews(false)
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

    override fun onResume() {
        super.onResume()
        toggleViews(false)
        etIPAddress.setText(UriPrefs.firstAddressByQRCode, TextView.BufferType.EDITABLE)
        try {
            presenter.onStop()
        } catch (e: UninitializedPropertyAccessException) {
            Log.d("onResume: Presenter was accessed when not initialized")
        }
    }

    override fun onStop() {
        presenter.onStop()
        super.onStop()
    }

    override fun context(): Context = this@MainActivity

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter = MainPresenter(this)
        inputMethodManager.hideSoftInputFromWindow(layoutSecond.windowToken, 0)
        presenter.askConnection()
        toggleViews(true)
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
            if (isEmergency) {
                tvEmergencyMode.visibility = android.view.View.VISIBLE
                tvEmergencyMode.text = "EMERGENCY MODE - GO TO ${route.last().id.name.capitalize()}"
            }
        }
    }

    override fun invalidateRoute(isEmergency: Boolean, showToast: Boolean) {
        runOnUiThread {
            val message: String = when (isEmergency) {
                false -> "You reached your destination"
                true -> "The emergency's done"
            }
            drawView.undrowRoute()
            tvEmergencyMode.visibility = View.GONE
            if (showToast) Snackbar.make(layoutSecond, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onSignalStrengthUpdated(strength: SIGNAL_STRENGTH) {
        runOnUiThread {
            tvSignalStrength.text = strength.text
            tvSignalStrength.textColor = strength.tint
        }
    }

    override fun onCellUpdated(cell: RoomViewedFromAUser) {
        runOnUiThread { tvCurrentRoom.text = cell.info.id.name }
    }

    override fun onShutdownReceived() {
        runOnUiThread {
            toast("The System appears to have shut down")
            tvEmergencyMode.visibility = android.view.View.VISIBLE
            tvEmergencyMode.text = "CAN'T CONNECT TO HOST"
        }
    }

    override fun toggleViews(areaOn: Boolean) {
        when (areaOn) {
            true -> {
                layoutFirst.visibility = View.GONE
                layoutSecond.visibility = View.VISIBLE
            }
            false -> {
                layoutFirst.visibility = View.VISIBLE
                layoutSecond.visibility = View.GONE
            }
        }
    }

    private fun detectedMovement(direction: Direction) {
        vibrator.vibrate(20)
        val maxStepsAtOnce = 10
        for (i in 0 until maxStepsAtOnce) {
            presenter.onMovementDetected(direction)
        }
    }

    private fun showRouteDialog() {

        val rooms = AreaState.area?.rooms?.map { (info) -> info.id.name }?.sorted()

        var departure = ""
        var arrival = ""

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

