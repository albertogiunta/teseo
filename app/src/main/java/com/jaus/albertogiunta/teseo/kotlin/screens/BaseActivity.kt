package com.jaus.albertogiunta.teseo.kotlin.screens

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jaus.albertogiunta.teseo.BuildConfig
import trikita.log.Log

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.level(Log.D)

        if (!BuildConfig.DEBUG) {
            @Suppress("INACCESSIBLE_TYPE")
            Log.usePrinter(Log.ANDROID, false)
        }
    }
}