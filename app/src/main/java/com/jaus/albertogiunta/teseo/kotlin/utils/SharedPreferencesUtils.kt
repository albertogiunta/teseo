package com.jaus.albertogiunta.teseo.kotlin.utils

import com.chibatching.kotpref.KotprefModel

object UriPrefs : KotprefModel() {
    var firstAddressByQRCode by stringPref("192.168.0.1:8081/uri1")
}