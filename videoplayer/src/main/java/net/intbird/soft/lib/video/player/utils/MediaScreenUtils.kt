package net.intbird.soft.lib.video.player.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
object MediaScreenUtils {
    fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.heightPixels
    }


    fun dp2px(context: Context, dip: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dip.toFloat() * scale + 0.5f * (if (dip >= 0) 1 else -1).toFloat()).toInt()
    }
}