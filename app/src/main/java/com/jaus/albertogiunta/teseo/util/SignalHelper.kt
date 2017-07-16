package com.jaus.albertogiunta.teseo.util

import com.jaus.albertogiunta.teseo.CellUpdateListener
import com.jaus.albertogiunta.teseo.UserPositionListener
import com.jaus.albertogiunta.teseo.data.Cell
import com.jaus.albertogiunta.teseo.data.InfoCell
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.util.SIGNAL_STRENGTH.*
import trikita.log.Log

class SignalHelper : UserPositionListener, CellUpdateListener {

    lateinit var cell: Cell
    lateinit var bestNewCandidate: InfoCell

    override fun onPositionChanged(userPosition: Point) {
        Log.d("update: riceived new userPosition! " + userPosition)

        when (calculateSignalStrength(userPosition)) {
            LOW -> bestNewCandidate = getNewBestCandidate(cell.neighbors, userPosition)
            VERY_LOW -> connectToNextBestHost()
        }
    }

    override fun onCellUpdated(cell: Cell) {
        this.cell = cell
        Log.d("update: riceived new cell! " + cell)
    }

    private fun calculateSignalStrength(userPosition: Point): SIGNAL_STRENGTH {
        return when (DistanceHelper.calculateDistanceBetweenPoints(userPosition, cell.infoCell.antennaPosition)) {
            in 0..1 -> STRONG
            in 2..3 -> MEDIUM
            in 3..5 -> LOW
            else -> VERY_LOW
        }
    }

    private fun getNewBestCandidate(candidates: List<InfoCell>, userPosition: Point): InfoCell {
        return candidates.map { it -> Pair(it, DistanceHelper.calculateDistanceBetweenPoints(userPosition, it.antennaPosition)) }.minBy { it -> it.second }!!.first
    }

    private fun connectToNextBestHost() {
        // TODO ask connection to next source
    }
}

enum class SIGNAL_STRENGTH {

    STRONG, MEDIUM, LOW, VERY_LOW

}