package com.naohstudio.compass.feature.model

import kotlin.math.roundToInt

class Coordinates(degree: Float) {

    init {
        if (!degree.isFinite()) {
            throw IllegalArgumentException("Degrees must be finite but was '$degree'")
        }
    }

    val degrees = normalizeAngle(degree)

    val roundedDegrees = normalizeAngle(degree.roundToInt().toFloat()).toInt()

    val descriptionDirection: DescriptionDirection = when (degrees) {
        in 22.5f .. 67.5f -> DescriptionDirection.NORTHEAST
        in 67.6f .. 112.5f -> DescriptionDirection.EAST
        in 112.6f .. 157.5f -> DescriptionDirection.SOUTHEAST
        in 157.6f .. 202.5f -> DescriptionDirection.SOUTH
        in 202.6f .. 247.5f -> DescriptionDirection.SOUTHWEST
        in 247.6f .. 292.5f -> DescriptionDirection.WEST
        in 292.6f .. 337.5f -> DescriptionDirection.NORTHWEST
        else -> DescriptionDirection.NORTH
    }

    private fun normalizeAngle(angleInDegrees: Float): Float {
        return (angleInDegrees + 360f) % 360f
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coordinates

        if (degrees != other.degrees) return false

        return true
    }

    override fun hashCode(): Int {
        return degrees.hashCode()
    }

    override fun toString(): String {
        return "Degree(degrees=$degrees)"
    }

    operator fun plus(degrees: Float) = Coordinates(this.degrees + degrees)

    operator fun minus(degrees: Float) = Coordinates(this.degrees - degrees)

    operator fun compareTo(coordinates: Coordinates) = this.degrees.compareTo(coordinates.degrees)
}