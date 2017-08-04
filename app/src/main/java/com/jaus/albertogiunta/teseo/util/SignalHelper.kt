package com.jaus.albertogiunta.teseo.util

import com.jaus.albertogiunta.teseo.CellSwitcherListener
import com.jaus.albertogiunta.teseo.CellUpdateListener
import com.jaus.albertogiunta.teseo.UserPositionListener
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.data.RoomViewedFromAUser
import com.jaus.albertogiunta.teseo.util.SIGNAL_STRENGTH.*
import trikita.log.Log

//class SignalHelper(val switch: CellSwitcherListener, val area: AreaViewedFromAUser) : UserPositionListener, CellUpdateListener {
class SignalHelper(val switch: CellSwitcherListener) : UserPositionListener, CellUpdateListener {

    lateinit var cell: RoomViewedFromAUser
    lateinit var bestNewCandidate: RoomViewedFromAUser

    override fun onPositionChanged(userPosition: Point) {
//        Log.d("update: riceived new userPosition! " + userPosition)

        try {
            when (calculateSignalStrength(userPosition)) {
                LOW -> bestNewCandidate = getNewBestCandidate(Unmarshalers.roomsListFromIDs(cell.neighbors, Unmarshalers.area!!), userPosition)
                VERY_LOW -> connectToNextBestCandidate()
            }
        } catch(e: UninitializedPropertyAccessException) {
            Log.d("onPositionChanged: got exception because not initialized")
        } catch (e: NullPointerException) {
            Log.d("onPositionChanged: got exception while trying to find bestNextCandidate ${cell.neighbors}")
        }
    }

    override fun onCellUpdated(cell: RoomViewedFromAUser) {
        this.cell = cell
        Log.d("update: received new cell!")
        Log.d("update: received new cell!")
    }

    private fun calculateSignalStrength(userPosition: Point): SIGNAL_STRENGTH {
        return when (DistanceHelper.doesPointLieInsideRectangle(userPosition, cell.info.roomVertices, 2)) {
            true -> STRONG
            false -> when (DistanceHelper.doesPointLieInsideRectangle(userPosition, cell.info.roomVertices, 1)) {
                true -> MEDIUM
                false -> when (DistanceHelper.doesPointLieInsideRectangle(userPosition, cell.info.roomVertices, 0)) {
                    true -> LOW
                    false -> VERY_LOW
                }
            }
        }
    }

    private fun getNewBestCandidate(candidates: List<RoomViewedFromAUser>, userPosition: Point): RoomViewedFromAUser {
        return candidates
                .map { it -> Pair(it, DistanceHelper.calculateDistanceBetweenPoints(userPosition, it.info.antennaPosition)) }
                .minBy { it -> it.second }!!
                .first
    }

    private fun connectToNextBestCandidate() {
        Log.d("connectToNextBestCandidate: signal very low")
        switch.onSwitchToCellRequested(bestNewCandidate)
    }
}

enum class SIGNAL_STRENGTH {
    STRONG, MEDIUM, NORMAL, LOW, VERY_LOW
}