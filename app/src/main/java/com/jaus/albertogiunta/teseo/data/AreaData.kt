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

data class Sensor(val category: Int,
                  val value: Double)

data class Cell(val infoCell: InfoCell,
                val sensors: List<Sensor>,
                val neighbors: List<InfoCell>,
                val passages: List<Passage>,
                val isEntryPoint: Boolean,
                val isExitPoint: Boolean,
                val capacity: Int,
                val squareMeters: Double,
                val currentPeople: Int,
                val practicabilityLevel: Double)

data class Area(val id: Int,
                val cells: List<Cell>)
