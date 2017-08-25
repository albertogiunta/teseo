package com.jaus.albertogiunta.teseo.kotlin.screens.initialSetup

import android.os.Bundle
import com.jaus.albertogiunta.teseo.R
import com.jaus.albertogiunta.teseo.kotlin.screens.BaseActivity
import com.jaus.albertogiunta.teseo.kotlin.utils.UriPrefs
import github.nisrulz.qreader.QRDataListener
import github.nisrulz.qreader.QREader
import kotlinx.android.synthetic.main.activity_initial_setup.*

class InitialSetupActivity : BaseActivity() {

    private var qReader: QREader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial_setup)
        qReader = QREader.Builder(this, cameraView, QRDataListener { data ->
            UriPrefs.firstAddressByQRCode = data
            finish()
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(cameraView.height)
                .width(cameraView.width)
                .build()
    }

    override fun onResume() {
        super.onResume()
        qReader?.initAndStart(cameraView)
        if (!qReader?.isCameraRunning!!) {
            qReader?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        qReader?.releaseAndCleanup()
    }
}
