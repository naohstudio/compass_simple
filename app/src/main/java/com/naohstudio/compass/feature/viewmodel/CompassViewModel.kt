package com.naohstudio.compass.feature.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.GeomagneticField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naohstudio.compass.feature.data.IpLocationService
import com.naohstudio.compass.feature.model.Coordinates
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CompassViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val PREFS_NAME = "compass_prefs"
        private const val KEY_DECLINATION = "declination"

        // Low-pass factor: lower = smoother but slower, close to the feel of the iOS compass
        private const val SMOOTHING_FACTOR = 0.15f

        // Skip UI updates smaller than this to avoid relayout on every sensor tick
        private const val MIN_DISPLAY_CHANGE = 0.1f
    }

    val coordinates = MutableLiveData<Coordinates>()
    val trueNorth = MutableLiveData(false)

    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Angle between magnetic north and true north at the user's location, null when unknown
    private var declination: Float? =
        if (prefs.contains(KEY_DECLINATION)) prefs.getFloat(KEY_DECLINATION, 0f) else null

    val hasLocation: Boolean
        get() = declination != null

    private var smoothSin = 0f
    private var smoothCos = 0f
    private var hasSmoothedValue = false

    init {
        trueNorth.value = prefs.contains(KEY_DECLINATION)
        loadDeclination()
    }

    private fun loadDeclination() {
        viewModelScope.launch {
            val location = IpLocationService.fetch() ?: return@launch
            val field = GeomagneticField(
                location.latitude.toFloat(),
                location.longitude.toFloat(),
                0f,
                System.currentTimeMillis()
            )
            declination = field.declination
            prefs.edit().putFloat(KEY_DECLINATION, field.declination).apply()
            trueNorth.value = true
        }
    }

    fun updateMagneticAzimuth(magneticAzimuth: Float) {
        val trueAzimuth = Math.toRadians((magneticAzimuth + (declination ?: 0f)).toDouble())

        // Filter on sin/cos so the 359° -> 0° wrap does not make the needle spin around
        val sinValue = sin(trueAzimuth).toFloat()
        val cosValue = cos(trueAzimuth).toFloat()
        if (hasSmoothedValue) {
            smoothSin += SMOOTHING_FACTOR * (sinValue - smoothSin)
            smoothCos += SMOOTHING_FACTOR * (cosValue - smoothCos)
        } else {
            smoothSin = sinValue
            smoothCos = cosValue
            hasSmoothedValue = true
        }

        val smoothed = Coordinates(Math.toDegrees(atan2(smoothSin, smoothCos).toDouble()).toFloat())
        val current = coordinates.value
        if (current == null || angleDistance(current.degrees, smoothed.degrees) >= MIN_DISPLAY_CHANGE) {
            coordinates.value = smoothed
        }
    }

    private fun angleDistance(a: Float, b: Float): Float {
        val diff = abs(a - b) % 360f
        return if (diff > 180f) 360f - diff else diff
    }
}
