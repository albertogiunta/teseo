package com.jaus.albertogiunta.teseo.kotlin

import android.app.Application
import com.chibatching.kotpref.Kotpref

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
    }

}