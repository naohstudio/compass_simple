package com.naohstudio.compass.feature.model

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.naohstudio.compass.R

@Keep
enum class DescriptionDirection(@StringRes val labelResourceId: Int) {
    NORTH(R.string.des_direction_north),
    NORTHEAST(R.string.des_direction_northeast),
    EAST(R.string.des_direction_east),
    SOUTHEAST(R.string.des_direction_southeast),
    SOUTH(R.string.des_direction_south),
    SOUTHWEST(R.string.des_direction_southwest),
    WEST(R.string.des_direction_west),
    NORTHWEST(R.string.des_direction_northwest)
}
