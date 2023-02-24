package com.adsmanager.startio

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.adsmanager.core.CallbackAds
import com.adsmanager.core.SizeBanner
import com.adsmanager.core.SizeNative
import com.adsmanager.core.iadsmanager.IAds
import com.adsmanager.core.iadsmanager.IInitialize
import com.adsmanager.core.rewards.IRewards
import com.bumptech.glide.Glide
import com.startapp.sdk.ads.banner.Banner
import com.startapp.sdk.ads.banner.BannerListener
import com.startapp.sdk.ads.nativead.NativeAdDetails
import com.startapp.sdk.ads.nativead.StartAppNativeAd
import com.startapp.sdk.adsbase.Ad
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener
import com.startapp.sdk.adsbase.adlisteners.AdEventListener


class StartIoAds : IAds {
    override fun initialize(context: Context, appId: String?, iInitialize: IInitialize?) {
        appId?.let {
            StartAppSDK.init(context, it, false)
            StartAppAd.disableSplash()
            StartAppSDK.setUserConsent(
                context,
                "pas",
                System.currentTimeMillis(),
                true
            )
        }
        iInitialize?.onInitializationComplete()
    }

    override fun loadGdpr(activity: Activity, childDirected: Boolean) {
        StartAppSDK.setUserConsent(
            activity,
            "pas",
            System.currentTimeMillis(),
            true
        )
    }

    private var startAppAd: StartAppAd? = null
    override fun loadInterstitial(activity: Activity, adUnitId: String) {
        startAppAd = StartAppAd(activity)
    }

    override fun loadRewards(activity: Activity, adUnitId: String) {

    }

    override fun setTestDevices(activity: Activity, testDevices: List<String>) {

    }

    override fun showBanner(
        activity: Activity,
        bannerView: RelativeLayout,
        sizeBanner: SizeBanner,
        adUnitId: String,
        callbackAds: CallbackAds?
    ) {
        val bannerAds  = Banner(activity, object : BannerListener {
            override fun onReceiveAd(p0: View?) {
                callbackAds?.onAdLoaded()
            }

            override fun onFailedToReceiveAd(p0: View?) {
                callbackAds?.onAdFailedToLoad("$p0")
            }

            override fun onImpression(p0: View?) {

            }

            override fun onClick(p0: View?) {

            }
        })
        val bannerParameters = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL)
        bannerView.removeAllViews()
        bannerView.addView(bannerAds, bannerParameters)
    }

    override fun showInterstitial(activity: Activity, adUnitId: String, callbackAds: CallbackAds?) {
        startAppAd?.showAd(object : AdDisplayListener {
            override fun adHidden(ad: Ad) {}
            override fun adDisplayed(ad: Ad) {
                callbackAds?.onAdLoaded()
            }
            override fun adClicked(ad: Ad) {}
            override fun adNotDisplayed(ad: Ad) {
                callbackAds?.onAdFailedToLoad(ad.toString())
            }
        })
    }

    @SuppressLint("InflateParams")
    override fun showNativeAds(
        activity: Activity,
        nativeView: RelativeLayout,
        sizeNative: SizeNative,
        adUnitId: String,
        callbackAds: CallbackAds?
    ) {
        val startAppNativeAd =  StartAppNativeAd(activity)
        when(sizeNative) {
            SizeNative.SMALL -> {
                val adViewNative = activity.layoutInflater.inflate(R.layout.startapp_small_native, null) as View
                val adListener: AdEventListener = object : AdEventListener {
                    // Callback Listener
                    override fun onReceiveAd(arg0: Ad) {
                        callbackAds?.onAdLoaded()
                        // Native Ad received
                        val ads: ArrayList<*> = startAppNativeAd.nativeAds // get NativeAds list

                        val iterator: Iterator<*> = ads.iterator()
                        while (iterator.hasNext()) {
                            Log.d("MyApplication", iterator.next().toString())
                        }
                        val adDetails = ads[0] as NativeAdDetails
                        val title = adViewNative.findViewById<TextView>(R.id.ad_headline)
                        title.text = adDetails.title
                        val icon = adViewNative.findViewById<ImageView>(R.id.ad_app_icon)
                        Glide.with(activity).load(adDetails.secondaryImageUrl).into(icon)
                        val description = adViewNative.findViewById<TextView>(R.id.ad_body)
                        description.text = adDetails.description
                        val open = adViewNative.findViewById<Button>(R.id.ad_call_to_action)
                        open.text = if (adDetails.isApp) "Install" else "Open"
                        adDetails.registerViewForInteraction(adViewNative)
                    }

                    override fun onFailedToReceiveAd(arg0: Ad?) {
                        // Native Ad failed to receive
                        callbackAds?.onAdFailedToLoad("Error while loading Ad: $arg0")
                    }
                }
                startAppNativeAd.loadAd(adListener)
                nativeView.removeAllViews()
                nativeView.addView(adViewNative)
            }
            SizeNative.MEDIUM -> {
                val adViewNative = activity.layoutInflater.inflate(R.layout.startapp_medium_native, null) as View
                // Declare Ad Callbacks Listener
                val adListener: AdEventListener = object : AdEventListener {
                    // Callback Listener
                    override fun onReceiveAd(arg0: Ad) {
                        callbackAds?.onAdLoaded()
                        // Native Ad received
                        val ads: ArrayList<*> = startAppNativeAd.nativeAds // get NativeAds list

                        val iterator: Iterator<*> = ads.iterator()
                        while (iterator.hasNext()) {
                            Log.d("MyApplication", iterator.next().toString())
                        }
                        val adDetails = ads[0] as NativeAdDetails
                        val title = adViewNative.findViewById<TextView>(R.id.ad_headline)
                        title.text = adDetails.title
                        val icon = adViewNative.findViewById<ImageView>(R.id.ad_app_icon)
                        Glide.with(activity).load(adDetails.secondaryImageUrl).into(icon)
                        val details = adViewNative.findViewById<ImageView>(R.id.imgDetail)
                        //Glide.with(activity).load(adDetails.getImageUrl()).centerCrop().fit().into(details);
                        Glide.with(activity).load(adDetails.imageUrl).into(details)
                        val description = adViewNative.findViewById<TextView>(R.id.ad_body)
                        description.text = adDetails.description
                        val open = adViewNative.findViewById<Button>(R.id.ad_call_to_action)
                        open.text = if (adDetails.isApp) "Install" else "Open"
                        adDetails.registerViewForInteraction(adViewNative)
                    }

                    override fun onFailedToReceiveAd(arg0: Ad?) {
                        // Native Ad failed to receive
                        callbackAds?.onAdFailedToLoad("Error while loading Ad: $arg0")
                    }
                }
                startAppNativeAd.loadAd(adListener)
                nativeView.removeAllViews()
                nativeView.addView(adViewNative)
            }
            SizeNative.SMALL_RECTANGLE -> {
                val adViewNative = activity.layoutInflater.inflate(R.layout.startapp_small_rectangle_native, null) as View
                val adListener: AdEventListener = object : AdEventListener {
                    // Callback Listener
                    override fun onReceiveAd(arg0: Ad) {
                        callbackAds?.onAdLoaded()
                        // Native Ad received
                        val ads: ArrayList<*> = startAppNativeAd.nativeAds // get NativeAds list

                        val iterator: Iterator<*> = ads.iterator()
                        while (iterator.hasNext()) {
                            Log.d("MyApplication", iterator.next().toString())
                        }
                        val adDetails = ads[0] as NativeAdDetails
                        val title = adViewNative.findViewById<TextView>(R.id.ad_headline)
                        title.text = adDetails.title
                        val icon = adViewNative.findViewById<ImageView>(R.id.ad_app_icon)
                        Glide.with(activity).load(adDetails.secondaryImageUrl).into(icon)
                        val description = adViewNative.findViewById<TextView>(R.id.ad_body)
                        description.text = adDetails.description
                        val open = adViewNative.findViewById<Button>(R.id.ad_call_to_action)
                        open.text = if (adDetails.isApp) "Install" else "Open"
                        adDetails.registerViewForInteraction(adViewNative)
                    }

                    override fun onFailedToReceiveAd(arg0: Ad?) {
                        // Native Ad failed to receive
                        callbackAds?.onAdFailedToLoad("Error while loading Ad: $arg0")
                    }
                }
                startAppNativeAd.loadAd(adListener)
                nativeView.removeAllViews()
                nativeView.addView(adViewNative)
            }
        }
    }

    override fun showRewards(
        activity: Activity,
        adUnitId: String,
        callbackAds: CallbackAds?,
        iRewards: IRewards?
    ) {
        callbackAds?.onAdFailedToLoad("rewards startIo not available")
    }


}