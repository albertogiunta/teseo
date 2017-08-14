package com.jaus.albertogiunta.teseo.helpers

import com.chibatching.kotpref.KotprefModel

object SavedCellUri : KotprefModel() {
    var uri by stringPref(":8081/uri1")
}