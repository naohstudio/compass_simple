package com.naohstudio.compass.feature.viewmodel

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naohstudio.compass.feature.model.Coordinates

class CompassViewModel : ViewModel() {
    val coordinates = MutableLiveData<Coordinates>()
    val trueNorth = MutableLiveData(false)
}
