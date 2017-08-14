package com.jaus.albertogiunta.teseo.helpers

import android.graphics.Color
import com.jaus.albertogiunta.teseo.CellUpdateListener
import com.jaus.albertogiunta.teseo.SignalAndCellSwitchingListener
import com.jaus.albertogiunta.teseo.UserPositionListener
import com.jaus.albertogiunta.teseo.data.AreaState
import com.jaus.albertogiunta.teseo.data.Point
import com.jaus.albertogiunta.teseo.data.RoomViewedFromAUser
import com.jaus.albertogiunta.teseo.helpers.SIGNAL_STRENGTH.*
import com.jaus.albertogiunta.teseo.util.IDExtractor
import trikita.log.Log

class SignalHelper(val signalListener: SignalAndCellSwitchingListener) : UserPositionListener, CellUpdateListener {

    lateinit var cell: RoomViewedFromAUser
    lateinit var bestNewCandidate: RoomViewedFromAUser

    override fun onPositionChanged(userPosition: Point) {
        try {
            val strength: SIGNAL_STRENGTH = calculateSignalStrength(userPosition)
            signalListener.onSignalStrengthUpdated(strength)
            when (strength) {
                STRONG -> Unit
                MEDIUM -> Unit
                LOW -> bestNewCandidate = getNewBestCandidate(IDExtractor.roomsListFromIDs(cell.neighbors, AreaState.area!!), userPosition)
                VERY_LOW -> connectToNextBestCandidate()
            }
        } catch(e: UninitializedPropertyAccessException) {
            Log.d("onPositionChanged: got exception because not initialized")
        } catch (e: NullPointerException) {
            Log.d("onPositionChanged: got exception while trying to find bestNextCandidate ${cell.neighbors}")
        }
    }

    override fun onCellUpdated(cell: RoomViewedFromAUser) {
        Log.d("onCellUpdated: received new cell!")
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

enum class SIGNAL_STRENGTH(val tint: Int, val text: String) {
    STRONG(Color.GREEN, "STRONG"),
    MEDIUM(Color.YELLOW, "MEDIUM"),
    LOW(Color.RED, "LOW"),
    VERY_LOW(Color.BLACK, "VERY_LOW")
}