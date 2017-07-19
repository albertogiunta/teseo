package com.jaus.albertogiunta.teseo.util

import com.jaus.albertogiunta.teseo.data.Area
import com.jaus.albertogiunta.teseo.data.*
import com.jaus.albertogiunta.teseo.data.Point


interface Observer<T : Any> {
    fun update(subject: Subject<T>)
}

open class Subject<T : Any> {

    lateinit var state: T

    val observers: MutableList<Observer<T>> = mutableListOf()

    fun attach(observer: Observer<T>) {
        observers.add(observer)
    }

    fun detach(observer: Observer<T>) {
        observers.remove(observer)
    }

    fun notifyObservers() {
        observers.forEach { it.update(this) }
    }
}

class PositionUpdateSubject : Subject<PositionUpdateSubject>() {

    init {
        state = this
    }

    var position: Point? = null
        set(value) {
            field = value
            notifyObservers()
        }
}


class AreaUpdateSubject : Subject<AreaUpdateSubject>() {

    init {
        state = this
    }

    var area: Area? = null
        set(value) {
            field = value
            notifyObservers()
        }
}

class CellUpdateSubject : Subject<CellUpdateSubject>() {

    init {
        state = this
    }

    var cell: CellForCell? = null
        set(value) {
            field = value
            notifyObservers()
        }
}