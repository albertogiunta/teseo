package com.jaus.albertogiunta.teseo.data

object AreaState {

    var area: AreaViewedFromAUser? = null

}

data class RoomID(val serial: Int,
                  val name: String)

data class CellInfo(val uri: String,
                    val port: Int)

data class RoomInfo(val id: RoomID,
                    val roomVertices: Coordinates,
                    val antennaPosition: Point,
                    val isEntryPoint: Boolean,
                    val isExitPoint: Boolean,
                    val capacity: Int,
                    val squareMeters: Double)


data class Point(var x: Int,
                 var y: Int) : Any()

data class Coordinates(val northWest: Point,
                       val northEast: Point,
                       val southWest: Point,
                       val southEast: Point)

data class Passage(val neighborId: Int,
                   val startCoordinates: Point,
                   val endCoordinates: Point)

data class RoomViewedFromAUser(val info: RoomInfo,
                               val cell: CellInfo,
                               val neighbors: List<RoomID>,
                               val passages: List<Passage>)

data class AreaViewedFromAUser(val id: Int,
                               val rooms: List<RoomViewedFromAUser>)

data class RouteResponseShort(val route: List<RoomID>)
