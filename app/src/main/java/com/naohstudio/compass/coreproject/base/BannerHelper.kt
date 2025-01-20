package com.naohstudio.compass.coreproject.base

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.naohstudio.compass.databinding.LayoutBannerAdBinding

class BannerHelper( private val activity: BaseActivity<*>) {

    var adView: AdView? = null
    var adSize: AdSize? = null

    fun loadBanner(binding: LayoutBannerAdBinding) {
        if (!Ads.canRequestAds(activity)){
            binding.root.gone()
            return
        }
        binding.root.visible()
        binding.shimmerFrameLayout.showShimmer(true)

        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density

        var adWidthPixels = binding.root.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()

        adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
        adView = AdView(activity)

        adView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                activity.lifecycleScope.launchWhenResumed {
                    binding.shimmerFrameLayout.apply {
                        stopShimmer()
                        hideShimmer()
                    }
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                activity.lifecycleScope.launchWhenResumed {
                    binding.root.gone()
                    binding.shimmerFrameLayout.apply {
                        stopShimmer()
                        hideShimmer()
                    }
                }
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
        binding.shimmerFrameLayout.addView(adView)
        adView?.adUnitId = Ads.getBannerAds()
        adView?.setAdSize(adSize!!)
        adView?.loadAd(AdRequest.Builder().build())
    }
}