package com.jaus.albertogiunta.teseo.util

import com.jaus.albertogiunta.teseo.CellUpdateListener
import com.jaus.albertogiunta.teseo.R
import com.jaus.albertogiunta.teseo.SignalListener
import com.jaus.albertogiunta.teseo.UserPositionListener
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.data.RoomViewedFromAUser
import com.jaus.albertogiunta.teseo.util.SIGNAL_STRENGTH.*
import trikita.log.Log

class SignalHelper(val signalListener: SignalListener) : UserPositionListener, CellUpdateListener {

    lateinit var cell: RoomViewedFromAUser
    lateinit var bestNewCandidate: RoomViewedFromAUser

    override fun onPositionChanged(userPosition: Point) {
        try {
            val strength: SIGNAL_STRENGTH = calculateSignalStrength(userPosition)
            signalListener.onSignalStrengthUpdated(strength)
            when (strength) {
                STRONG -> Unit
                MEDIUM -> Unit
                LOW -> bestNewCandidate = getNewBestCandidate(Unmarshaler.roomsListFromIDs(cell.neighbors, AreaState.area!!), userPosition)
                VERY_LOW -> connectToNextBestCandidate()
            }
        } catch(e: UninitializedPropertyAccessException) {
            Log.d("onPositionChanged: got exception because not initialized")
        } catch (e: NullPointerException) {
            Log.d("onPositionChanged: got exception while trying to find bestNextCandidate ${cell.neighbors}")
        }
    }

    override fun onCellUpdated(cell: RoomViewedFromAUser) {
        Log.d("update: received new cell!")
        this.cell = cell
        this.bestNewCandidate = cell
    }

    private fun calculateSignalStrength(userPosition: Point): SIGNAL_STRENGTH {
        return when (DistanceHelper.doesPointLieInsideRectangle(userPosition, cell.info.roomVertices, 40)) {
            true -> STRONG
            false -> when (DistanceHelper.doesPointLieInsideRectangle(userPosition, cell.info.roomVertices, 20)) {
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
        signalListener.onSwitchToCellRequested(bestNewCandidate)
    }
}

enum class SIGNAL_STRENGTH(val tint: Int) {
    STRONG(R.color.signal_strong),
    MEDIUM(R.color.signal_medium),
    LOW(R.color.signal_low),
    VERY_LOW(R.color.signal_very_low)
}