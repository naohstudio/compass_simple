package com.naohstudio.compass.feature.model

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.naohstudio.compass.R

enum class CompassStyle(
    @DrawableRes val backgroundResId: Int,
    @StringRes val labelResId: Int,
    @ColorInt val roseTextColor: Int?
) {
    CLASSIC(R.drawable.compass_background, R.string.compass_style_classic, null),
    DARK(R.drawable.compass_background_dark, R.string.compass_style_dark, 0xFFF5E6B8.toInt()),
    NAUTICAL(R.drawable.compass_background_nautical, R.string.compass_style_nautical, 0xFF5A3720.toInt()),
    NEON(R.drawable.compass_background_neon, R.string.compass_style_neon, 0xFF7FF3FF.toInt()),
    FOREST(R.drawable.compass_background_forest, R.string.compass_style_forest, 0xFFE8F5E9.toInt()),
    OCEAN(R.drawable.compass_background_ocean, R.string.compass_style_ocean, 0xFFFFFFFF.toInt()),
    SUNSET(R.drawable.compass_background_sunset, R.string.compass_style_sunset, 0xFF8D2A00.toInt()),
    ROYAL(R.drawable.compass_background_royal, R.string.compass_style_royal, 0xFFF3E5F5.toInt()),
    MILITARY(R.drawable.compass_background_military, R.string.compass_style_military, 0xFFF0F0D0.toInt()),
    ICE(R.drawable.compass_background_ice, R.string.compass_style_ice, 0xFF01579B.toInt());

    companion object {
        fun fromName(name: String?): CompassStyle = values().firstOrNull { it.name == name } ?: CLASSIC
    }
}
