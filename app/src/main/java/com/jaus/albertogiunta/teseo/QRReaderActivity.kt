package com.jaus.albertogiunta.teseo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jaus.albertogiunta.teseo.util.SavedCellUri
import github.nisrulz.qreader.QRDataListener
import github.nisrulz.qreader.QREader
import kotlinx.android.synthetic.main.activity_qrreader.*
import trikita.log.Log

class QRReaderActivity : AppCompatActivity() {

    private var qReader: QREader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrreader)
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
