package com.jaus.albertogiunta.teseo.util

import com.jaus.albertogiunta.teseo.CellSwitch
import com.jaus.albertogiunta.teseo.CellUpdateListener
import com.jaus.albertogiunta.teseo.UserPositionListener
import com.jaus.albertogiunta.teseo.data.CellForCell
import com.jaus.albertogiunta.teseo.data.InfoCell
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.util.SIGNAL_STRENGTH.*
import trikita.log.Log

class SignalHelper(val switch: CellSwitch) : UserPositionListener, CellUpdateListener {

    lateinit var cell: CellForCell
    lateinit var bestNewCandidate: InfoCell

    override fun onPositionChanged(userPosition: Point) {
        Log.d("update: riceived new userPosition! " + userPosition)

        when (calculateSignalStrength(userPosition)) {
            LOW -> bestNewCandidate = getNewBestCandidate(cell.neighbors, userPosition)
            VERY_LOW -> connectToNextBestCandidate()
        }
    }

    override fun onCellUpdated(cell: CellForCell) {
        this.cell = cell
        Log.d("update: received new cell! " + cell)
    }

    private fun calculateSignalStrength(userPosition: Point): SIGNAL_STRENGTH {
        return when (DistanceHelper.doesPointLieInsideRectangle(userPosition, cell.infoCell.roomVertices, 3)) {
            true -> NORMAL
            false -> when (DistanceHelper.doesPointLieInsideRectangle(userPosition, cell.infoCell.roomVertices, 2)) {
                true -> LOW
                false -> when (DistanceHelper.doesPointLieInsideRectangle(userPosition, cell.infoCell.roomVertices, 1)) {
                    true -> VERY_LOW
                    false -> VERY_LOW
                }
            }
        }
    }

    private fun getNewBestCandidate(candidates: List<InfoCell>, userPosition: Point): InfoCell {
        return candidates.map { it -> Pair(it, DistanceHelper.calculateDistanceBetweenPoints(userPosition, it.antennaPosition)) }.minBy { it -> it.second }!!.first
    }

    private fun connectToNextBestCandidate() {
        Log.d("connectToNextBestCandidate: signal very low")
//        switch.switchToCell(bestNewCandidate)
    }
}

enum class SIGNAL_STRENGTH {
    STRONG, MEDIUM, NORMAL, LOW, VERY_LOW
}