package com.jaus.albertogiunta.teseo

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
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

    fun Context.toast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}