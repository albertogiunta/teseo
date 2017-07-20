package com.jaus.albertogiunta.teseo.data

data class InfoCell(val id: Int,
                    val uri: String,
                    val name: String,
                    val roomVertices: Coordinates,
                    val antennaPosition: Point)

data class Point(var x: Int,
                 var y: Int) : Any()

data class Coordinates(val northWest: Point,
                       val northEast: Point,
                       val southWest: Point,
                       val southEast: Point)

data class Passage(val neighborId: Int,
                   val startCoordinates: Point,
                   val endCoordinates: Point)

data class CellForCell(val infoCell: InfoCell,
                       val neighbors: List<InfoCell>,
                       val passages: List<Passage>,
                       val isEntryPoint: Boolean,
                       val isExitPoint: Boolean,
                       val practicabilityLevel: Double)

data class Area(val id: Int,
                val cells: List<CellForCell>)


data class RouteRequestLight(val userID: String,
                             val fromCellId: Int,
                             val toCellId: Int)

data class RouteRequest(val userID: String,
                        val fromCell: InfoCell,
                        val toCell: InfoCell)

data class RouteResponse(val request: RouteRequest,
                         val route: List<InfoCell>)

data class EscapeResponse(val info: InfoCell,
                          val route: List<InfoCell>)
