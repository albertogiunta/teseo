package com.jaus.albertogiunta.teseo.screens.initialSetup

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jaus.albertogiunta.teseo.R
import com.jaus.albertogiunta.teseo.helpers.SavedCellUri
import github.nisrulz.qreader.QRDataListener
import github.nisrulz.qreader.QREader
import kotlinx.android.synthetic.main.activity_initial_setup.*
import trikita.log.Log

class InitialSetupActivity : AppCompatActivity() {

    private var qReader: QREader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial_setup)
        qReader = QREader.Builder(this, cameraView, QRDataListener { data ->
            Log.d("Value read from QRCode: " + data)
            SavedCellUri.uri = data
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
