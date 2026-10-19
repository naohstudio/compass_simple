package com.naohstudio.compass.feature.model

class RotationVector(values: FloatArray) {

    // Keep all components (x, y, z and the scalar w when present) for a precise rotation matrix
    val values: FloatArray = values.copyOf(minOf(values.size, 5))
}
