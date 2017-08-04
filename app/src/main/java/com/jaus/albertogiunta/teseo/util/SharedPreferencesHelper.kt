package com.jaus.albertogiunta.teseo.util

import com.chibatching.kotpref.KotprefModel

object SavedCellUri : KotprefModel() {
    var uri by stringPref(":8081/uri1")
}