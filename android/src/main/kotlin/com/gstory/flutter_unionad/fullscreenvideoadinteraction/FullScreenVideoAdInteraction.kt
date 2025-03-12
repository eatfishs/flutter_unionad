package com.gstory.flutter_unionad.fullscreenvideoadinteraction

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot
import com.gstory.flutter_unionad.EcpmUtil
import com.gstory.flutter_unionad.FlutterUnionadEventPlugin

/**
 * @Description:
 * @Author: gstory0404@gmail
 * @CreateDate: 2020/8/21 15:48
 */
@SuppressLint("StaticFieldLeak")
object FullScreenVideoAdInteraction {
    private var TAG = "FullScreenVideoExpressAd"
    var mContext: Context? = null
    var mActivity: Activity? = null
    private var mttFullVideoAd: TTFullScreenVideoAd? = null

    //广告所需参数
    private var mCodeId: String? = null
    private var orientation: Int = 1


    fun init(
        context: Context,
        mActivity: Activity,
        mCodeId: String?,
        orientation: Int?,
    ) {
        this.mContext = context
        this.mActivity = mActivity
        this.mCodeId = mCodeId
        this.orientation = orientation!!
        loadFullScreenVideoAd()
    }

    private fun loadFullScreenVideoAd() {
        Log.e(TAG, "广告位id  $mCodeId")
        //设置广告参数
        val adSlot = AdSlot.Builder().setCodeId(mCodeId) //广告位id
            .setOrientation(orientation).setMediationAdSlot(
                MediationAdSlot.Builder().setMuted(true)//是否静音
                    .setVolume(0.5f)//设置音量
                    .setBidNotify(true)//竞价结果通知
                    .build()
            ).build()
        var mTTAdNative = TTAdSdk.getAdManager().createAdNative(mActivity)
        //加载全屏视频
        mTTAdNative.loadFullScreenVideoAd(adSlot, object : TTAdNative.FullScreenVideoAdListener {
            override fun onError(code: Int, message: String) {
                Log.e(TAG, "fullScreenVideoAd加载失败  $code === > $message")
                var map: MutableMap<String, Any?> = mutableMapOf(
                    "adType" to "fullScreenVideoAdInteraction",
                    "onAdMethod" to "onFail",
                    "error" to "$code , $message"
                )
                FlutterUnionadEventPlugin.sendContent(map)
            }

            override fun onFullScreenVideoAdLoad(ad: TTFullScreenVideoAd) {
                Log.e(TAG, "fullScreenVideoAdInteraction loaded")
                mttFullVideoAd = ad
                var map: MutableMap<String, Any?> = mutableMapOf(
                    "adType" to "fullScreenVideoAdInteraction", "onAdMethod" to "onReady"
                )
                FlutterUnionadEventPlugin.sendContent(map)
            }

            override fun onFullScreenVideoCached() {
                Log.e(TAG, "fullScreenVideoAdInteraction video cached")
            }

            override fun onFullScreenVideoCached(p0: TTFullScreenVideoAd?) {
                Log.e(TAG, "fullScreenVideoAdInteraction video cached2")
            }
        })
    }

    fun showAd() {
        if (mttFullVideoAd == null) {
            var map: MutableMap<String, Any?> = mutableMapOf(
                "adType" to "fullScreenVideoAdInteraction",
                "onAdMethod" to "onUnReady",
                "error" to "广告预加载未完成"
            )
            FlutterUnionadEventPlugin.sendContent(map)
            return
        }
        mttFullVideoAd?.setFullScreenVideoAdInteractionListener(object :
            TTFullScreenVideoAd.FullScreenVideoAdInteractionListener {
            override fun onAdShow() {
                Log.e(TAG, "fullScreenVideoAdInteraction show")
                FlutterUnionadEventPlugin.sendContent(
                    mutableMapOf(
                        "adType" to "fullScreenVideoAdInteraction", "onAdMethod" to "onShow"
                    )
                )
                Log.d(TAG, "ecpm ${EcpmUtil.toMap(mttFullVideoAd?.mediationManager?.showEcpm)}")
                FlutterUnionadEventPlugin.sendContent(
                    mutableMapOf(
                        "adType" to "fullScreenVideoAdInteraction",
                        "onAdMethod" to "onEcpm",
                        "info" to EcpmUtil.toMap(mttFullVideoAd?.mediationManager?.showEcpm)
                    )
                )
            }

            override fun onAdVideoBarClick() {
                Log.e(TAG, "fullScreenVideoAd click")
                var map: MutableMap<String, Any?> = mutableMapOf(
                    "adType" to "fullScreenVideoAdInteraction", "onAdMethod" to "onClick"
                )
                FlutterUnionadEventPlugin.sendContent(map)
            }

            override fun onAdClose() {
                Log.e(TAG, "fullScreenVideoAd close")
                var map: MutableMap<String, Any?> = mutableMapOf(
                    "adType" to "fullScreenVideoAdInteraction", "onAdMethod" to "onClose"
                )
                FlutterUnionadEventPlugin.sendContent(map)
            }

            override fun onVideoComplete() {
                Log.e(TAG, "fullScreenVideoAd complete")
                var map: MutableMap<String, Any?> = mutableMapOf(
                    "adType" to "fullScreenVideoAdInteraction", "onAdMethod" to "onFinish"
                )
                FlutterUnionadEventPlugin.sendContent(map)
            }

            override fun onSkippedVideo() {
                Log.e(TAG, "fullScreenVideoAd skipped")
                var map: MutableMap<String, Any?> = mutableMapOf(
                    "adType" to "fullScreenVideoAdInteraction", "onAdMethod" to "onSkip"
                )
                FlutterUnionadEventPlugin.sendContent(map)
            }
        })
        mttFullVideoAd?.showFullScreenVideoAd(mActivity)
    }
}