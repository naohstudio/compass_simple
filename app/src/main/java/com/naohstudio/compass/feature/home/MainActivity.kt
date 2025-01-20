package com.naohstudio.compass.feature.home

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.Surface
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.naohstudio.compass.coreproject.base.BannerHelper
import com.naohstudio.compass.coreproject.base.BaseActivity
import com.naohstudio.compass.coreproject.base.GoogleMobileAdsConsentManager
import com.naohstudio.compass.databinding.ActivityMainBinding
import com.naohstudio.compass.feature.model.Coordinates
import com.naohstudio.compass.feature.model.DisplayRotation
import com.naohstudio.compass.feature.model.RotationVector
import com.naohstudio.compass.feature.utils.MathUtils
import com.naohstudio.compass.feature.viewmodel.CompassViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun bindingView() = ActivityMainBinding.inflate(layoutInflater)

    private val viewModel by viewModels<CompassViewModel>()

    private var sensorManager: SensorManager? = null

    private val compassSensorEventListener = CompassSensorEventListener()

    override fun initConfig(savedInstanceState: Bundle?) {
        super.initConfig(savedInstanceState)
        val googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(this)
        googleMobileAdsConsentManager.gatherConsent(this) {
            initBanner()
        }
        try {
            sensorManager = ActivityCompat.getSystemService(this@MainActivity, SensorManager::class.java)
            sensorManager?.let {
                it.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.also {sensor->
                    registerSensorListener(it, sensor)
                }
            }
        } catch (ex: Exception) {
            Log.d(TAG, "initConfig: init sensor failed")
        }
    }

    private fun initBanner() {
        val bannerHelper = BannerHelper(this@MainActivity)
        bannerHelper.loadBanner(binding.layoutBanner)
    }

    override fun initObserver() {
        super.initObserver()
        
        viewModel.coordinates.observe(this@MainActivity) {
            binding.compass.setDegree(it.degrees)
        }
    }

    override fun initListener() {
        super.initListener()

        binding.btnRate.setOnClickListener {
            linkAppStore()
        }
    }

    private fun linkAppStore() {
        val uri: Uri = Uri.parse("market://details?id=${this.packageName}")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            this.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            try {
                this.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=${this.packageName}")
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun registerSensorListener(sensorManager: SensorManager, rotationVectorSensor: Sensor) {
        val success = sensorManager.registerListener(
            compassSensorEventListener,
            rotationVectorSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        if (success) {
            Log.d(TAG, "Registered listener for rotation vector sensor")
        } else {
            Log.d(TAG, "Could not enable rotation vector sensor")
        }
    }

    private inner class CompassSensorEventListener : SensorEventListener {

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            when (sensor.type) {
                else -> Log.d(TAG, "Unexpected accuracy changed event of type ${sensor.type}")
            }
        }
        
        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_ROTATION_VECTOR -> updateCompass(event)
                else -> Log.w(TAG, "Unexpected sensor changed event of type ${event.sensor.type}")
            }
        }

        private fun updateCompass(event: SensorEvent) {
            val rotationVector = RotationVector(event.values[0], event.values[1], event.values[2])
            val displayRotation = getDisplayRotation()
            val magneticAth = MathUtils.calculateCoordinates(rotationVector, displayRotation)
            setDegree(magneticAth)

        }

        private fun getDisplayRotation(): DisplayRotation {
            return when (getDisplayCompat()?.rotation) {
                Surface.ROTATION_90 -> DisplayRotation.ROTATION_90
                Surface.ROTATION_180 -> DisplayRotation.ROTATION_180
                Surface.ROTATION_270 -> DisplayRotation.ROTATION_270
                else -> DisplayRotation.ROTATION_0
            }
        }

        private fun getDisplayCompat(): Display? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                display
            } else {
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay
            }
        }
    }

    internal fun setDegree(coordinates: Coordinates) {
        viewModel.coordinates.value = coordinates
    }
    
}