package com.naohstudio.compass.feature.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import androidx.annotation.AnyRes
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.naohstudio.compass.R
import com.naohstudio.compass.databinding.CompassViewBinding
import com.naohstudio.compass.feature.model.Coordinates
import com.naohstudio.compass.feature.utils.MathUtils

class CompassView(context: Context, attributes: AttributeSet) : ConstraintLayout(context, attributes) {

    companion object {

        private const val HAPTIC_FEEDBACK_INTERVAL = 2.0f
    }

    @IdRes
    private val center = R.id.imgCompass

    private var lastHapticFeedbackPoint: Coordinates? = null

    private var binding: CompassViewBinding

    init {
        val layoutInflater = LayoutInflater.from(context)
        binding = CompassViewBinding.inflate(layoutInflater, this, true)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        visibility = INVISIBLE
        updateStatusDegreesTextSize(width * getFloat(R.dimen.status_degrees_text_size_factor))
        updateStatusCardinalDirectionTextSize(width * getFloat(R.dimen.status_direction_text_size_factor))
        updateCardinalDirectionTextSize(width * getFloat(R.dimen.direction_text_size_factor))
        updateDegreeTextSize(width * getFloat(R.dimen.degree_text_size_factor))
    }

    private fun updateStatusDegreesTextSize(textSize: Float) {
        binding.statusDegreesText.setTextSize(COMPLEX_UNIT_PX, textSize)
    }

    private fun updateStatusCardinalDirectionTextSize(textSize: Float) {
        binding.statusCardinalDirectionText.setTextSize(COMPLEX_UNIT_PX, textSize)
    }

    private fun updateCardinalDirectionTextSize(textSize: Float) {
        binding.cardinalDirectionNorthText.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.cardinalDirectionEastText.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.cardinalDirectionSouthText.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.cardinalDirectionWestText.setTextSize(COMPLEX_UNIT_PX, textSize)
    }

    private fun updateDegreeTextSize(textSize: Float) {
        binding.degree0Text.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.degree30Text.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.degree60Text.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.degree90Text.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.degree120Text.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.degree150Text.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.degree180Text.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.degree210Text.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.degree240Text.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.degree270Text.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.degree300Text.setTextSize(COMPLEX_UNIT_PX, textSize)
        binding.degree330Text.setTextSize(COMPLEX_UNIT_PX, textSize)
    }

    fun setDegree(value: Float) {
        val coordinates = Coordinates(value)

        updateStatusDegreesText(coordinates)
        updateStatusDirectionText(coordinates)

        val rotation = coordinates.degrees.unaryMinus()
        rotateCompassRoseImage(rotation)
        rotateCompassRoseTexts(rotation)
        handleHapticFeedback(coordinates)

        visibility = VISIBLE
    }

    private fun updateStatusDegreesText(coordinates: Coordinates) {
        binding.statusDegreesText.text = context.getString(R.string.degrees, coordinates.roundedDegrees)
    }

    private fun updateStatusDirectionText(coordinates: Coordinates) {
        binding.statusCardinalDirectionText.text = context.getString(coordinates.descriptionDirection.labelResourceId)
    }

    private fun rotateCompassRoseImage(rotation: Float) {
        binding.imgCompass.rotation = rotation
    }

    private fun rotateCompassRoseTexts(rotation: Float) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.root)

        rotateCardinalDirectionTexts(constraintSet, rotation)
        rotateDegreeTexts(constraintSet, rotation)

        constraintSet.applyTo(binding.root)
    }

    private fun rotateCardinalDirectionTexts(constraintSet: ConstraintSet, rotation: Float) {
        val radius = calculateTextRadius(getFloat(R.dimen.cardinal_direction_text_ratio))

        constraintSet.constrainCircle(R.id.cardinal_direction_north_text, center, radius, rotation)
        constraintSet.constrainCircle(R.id.cardinal_direction_east_text, center, radius, rotation + 90)
        constraintSet.constrainCircle(R.id.cardinal_direction_south_text, center, radius, rotation + 180)
        constraintSet.constrainCircle(R.id.cardinal_direction_west_text, center, radius, rotation + 270)
    }

    private fun rotateDegreeTexts(constraintSet: ConstraintSet, rotation: Float) {
        val radius = calculateTextRadius(getFloat(R.dimen.degree_text_ratio))

        constraintSet.constrainCircle(R.id.degree_0_text, center, radius, rotation)
        constraintSet.constrainCircle(R.id.degree_30_text, center, radius, rotation + 30)
        constraintSet.constrainCircle(R.id.degree_60_text, center, radius, rotation + 60)
        constraintSet.constrainCircle(R.id.degree_90_text, center, radius, rotation + 90)
        constraintSet.constrainCircle(R.id.degree_120_text, center, radius, rotation + 120)
        constraintSet.constrainCircle(R.id.degree_150_text, center, radius, rotation + 150)
        constraintSet.constrainCircle(R.id.degree_180_text, center, radius, rotation + 180)
        constraintSet.constrainCircle(R.id.degree_210_text, center, radius, rotation + 210)
        constraintSet.constrainCircle(R.id.degree_240_text, center, radius, rotation + 240)
        constraintSet.constrainCircle(R.id.degree_270_text, center, radius, rotation + 270)
        constraintSet.constrainCircle(R.id.degree_300_text, center, radius, rotation + 300)
        constraintSet.constrainCircle(R.id.degree_330_text, center, radius, rotation + 330)
    }

    private fun getFloat(@AnyRes id: Int): Float {
        val tempValue = TypedValue()
        resources.getValue(id, tempValue, true)
        return tempValue.float
    }

    private fun calculateTextRadius(ratio: Float): Int {
        return width / 2 - (width * ratio).toInt()
    }

    private fun handleHapticFeedback(coordinates: Coordinates) {
        lastHapticFeedbackPoint
            ?.also { checkHapticFeedback(coordinates, it) }
            ?: run { updateLastHapticFeedbackPoint(coordinates) }
    }

    private fun checkHapticFeedback(coordinates: Coordinates, lastHapticFeedbackPoint: Coordinates) {
        val boundaryStart = lastHapticFeedbackPoint - HAPTIC_FEEDBACK_INTERVAL
        val boundaryEnd = lastHapticFeedbackPoint + HAPTIC_FEEDBACK_INTERVAL

        if (!MathUtils.isCoordinatesBetweenTwoPoints(coordinates, boundaryStart, boundaryEnd)) {
            updateLastHapticFeedbackPoint(coordinates)
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    private fun updateLastHapticFeedbackPoint(coordinates: Coordinates) {
        val closestIntervalPoint = MathUtils.getClosestNumberFromInterval(coordinates.degrees, HAPTIC_FEEDBACK_INTERVAL)
        lastHapticFeedbackPoint = Coordinates(closestIntervalPoint)
    }
}