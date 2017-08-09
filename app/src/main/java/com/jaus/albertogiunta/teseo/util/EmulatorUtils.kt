package com.jaus.albertogiunta.teseo.util

import android.os.Build

object EmulatorUtils {

    fun isOnEmulator(): Boolean {
        return Build.HARDWARE.contains("golfdish")
    }

}