package com.jaus.albertogiunta.teseo

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import trikita.log.Log

class MainActivity : BaseActivity() {

    val presenter : MainPresenter = MainPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.run()

        btnPrint.setOnClickListener {
            val str = "I want to connect"
            presenter.websocket.send(str)
            Log.d("onCreate: $str")
        }
    }
}