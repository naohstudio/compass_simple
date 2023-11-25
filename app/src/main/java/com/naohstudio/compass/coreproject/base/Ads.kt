package com.naohstudio.compass.coreproject.base

import com.naohstudio.compass.BuildConfig


object Ads {

    private const val BANNER_TEST_ADS = "ca-app-pub-3940256099942544/6300978111"

    fun getBannerAds(): String {
        return if (BuildConfig.DEBUG) {
            BANNER_TEST_ADS
        } else {
            "ca-app-pub-9344348136193481/6093065829"
        }
    }
}