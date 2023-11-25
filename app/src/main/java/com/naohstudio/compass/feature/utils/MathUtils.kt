package com.naohstudio.compass.feature.utils

import android.hardware.SensorManager
import com.naohstudio.compass.feature.model.Coordinates
import com.naohstudio.compass.feature.model.DisplayRotation
import com.naohstudio.compass.feature.model.RotationVector
import kotlin.math.roundToInt

object MathUtils {

    private const val ROOT_DEGREE = 0
    private const val AXIS_SIZE = 3
    private const val ROTATION_MATRIX_SIZE = 9

    @JvmStatic
    fun calculateCoordinates(rotationVector: RotationVector, displayRotation: DisplayRotation): Coordinates {
        val rotationMatrix = getRotationMatrix(rotationVector)
        val remappedRotationMatrix = remapRotationMatrix(rotationMatrix, displayRotation)
        val orientationInRadians = SensorManager.getOrientation(remappedRotationMatrix, FloatArray(AXIS_SIZE))
        val athInRadians = orientationInRadians[ROOT_DEGREE]
        val athInDegrees = Math.toDegrees(athInRadians.toDouble()).toFloat()
        return Coordinates(athInDegrees)
    }

    private fun getRotationMatrix(rotationVector: RotationVector): FloatArray {
        val rotationMatrix = FloatArray(ROTATION_MATRIX_SIZE)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector.toArray())
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