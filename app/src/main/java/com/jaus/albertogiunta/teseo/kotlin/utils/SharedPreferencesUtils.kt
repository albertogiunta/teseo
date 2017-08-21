package com.jaus.albertogiunta.teseo.kotlin.utils

import com.chibatching.kotpref.KotprefModel

object SavedCellUri : KotprefModel() {
    var uri by stringPref(":8081/uri1")
}