package com.jaus.albertogiunta.teseo.kotlin.utils

import com.chibatching.kotpref.KotprefModel

object UriPrefs : KotprefModel() {
    var uri by stringPref(":8081/uri1")
    var localIP by stringPref("192.168.0.1")
}