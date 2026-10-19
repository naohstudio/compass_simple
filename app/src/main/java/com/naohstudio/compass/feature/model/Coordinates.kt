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

    val descriptionDirection: DescriptionDirection =
        DescriptionDirection.values()[((degrees + 22.5f) / 45f).toInt() % 8]

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