package com.naohstudio.compass.feature.utils

import android.hardware.SensorManager
import com.naohstudio.compass.feature.model.Coordinates
import com.naohstudio.compass.feature.model.DisplayRotation
import com.naohstudio.compass.feature.model.RotationVector
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.roundToInt

object MathUtils {

    private const val ROOT_DEGREE = 0
    private const val AXIS_SIZE = 3
    private const val ROTATION_MATRIX_SIZE = 9

    /**
     * Heading like the iOS compass: the direction the top of the screen points when the phone
     * lies flat, and the direction the back camera points when the phone is held upright.
     * Using getOrientation() alone breaks down when the phone is upright (gimbal lock).
     */
    @JvmStatic
    fun calculateCoordinates(rotationVector: RotationVector, displayRotation: DisplayRotation): Coordinates {
        val rotationMatrix = getRotationMatrix(rotationVector)
        val r = remapRotationMatrix(rotationMatrix, displayRotation)

        // Rows of r are the world East / North / Up components of the device X, Y, Z axes
        val topHorizontal = hypot(r[1], r[4])
        val backHorizontal = hypot(r[2], r[5])
        val athInRadians = if (topHorizontal >= backHorizontal) {
            atan2(r[1], r[4])
        } else {
            atan2(-r[2], -r[5])
        }
        val athInDegrees = Math.toDegrees(athInRadians.toDouble()).toFloat()
        return Coordinates(athInDegrees)
    }

    /**
     * Original calculation: magnetic azimuth from getOrientation(), used when the location
     * (and therefore true north) is not available.
     */
    @JvmStatic
    fun calculateLegacyCoordinates(rotationVector: RotationVector, displayRotation: DisplayRotation): Coordinates {
        val rotationMatrix = getRotationMatrix(rotationVector)
        val remappedRotationMatrix = remapRotationMatrix(rotationMatrix, displayRotation)
        val orientationInRadians = SensorManager.getOrientation(remappedRotationMatrix, FloatArray(AXIS_SIZE))
        val athInRadians = orientationInRadians[ROOT_DEGREE]
        val athInDegrees = Math.toDegrees(athInRadians.toDouble()).toFloat()
        return Coordinates(athInDegrees)
    }

    private fun getRotationMatrix(rotationVector: RotationVector): FloatArray {
        val rotationMatrix = FloatArray(ROTATION_MATRIX_SIZE)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector.values)
        return rotationMatrix
    }

    private fun remapRotationMatrix(rotationMatrix: FloatArray, displayRotation: DisplayRotation): FloatArray {
        return when (displayRotation) {
            DisplayRotation.ROTATION_0 -> remapRotationMatrix(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y)
            DisplayRotation.ROTATION_90 -> remapRotationMatrix(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X)
            DisplayRotation.ROTATION_180 -> remapRotationMatrix(rotationMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y)
            else -> remapRotationMatrix(rotationMatrix, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X)
        }
    }

    private fun remapRotationMatrix(rotationMatrix: FloatArray, newX: Int, newY: Int): FloatArray {
        val remappedRotationMatrix = FloatArray(ROTATION_MATRIX_SIZE)
        SensorManager.remapCoordinateSystem(rotationMatrix, newX, newY, remappedRotationMatrix)
        return remappedRotationMatrix
    }

    fun getClosestNumberFromInterval(number: Float, interval: Float): Float =
        (number / interval).roundToInt() * interval


    fun isCoordinatesBetweenTwoPoints(coordinates: Coordinates, pointA: Coordinates, pointB: Coordinates): Boolean {
        val aToB = (pointB.degrees - pointA.degrees + 360f) % 360f
        val aToAth = (coordinates.degrees - pointA.degrees + 360f) % 360f
        return aToB <= 180f != aToAth > aToB
    }
}