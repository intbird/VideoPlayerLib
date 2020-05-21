package intbird.soft.lib.video.player.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.core.content.ContextCompat

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
object MediaLightUtils {

    private fun tunOffAutoBrightness(context: Context) {
        try {
            if (Settings.System.getInt(
                            context.contentResolver,
                            Settings.System.SCREEN_BRIGHTNESS_MODE
                    ) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            ) {
                Settings.System.putInt(
                        context.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    const val maxSystemBrightness = 255

    fun checkSystemWritePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(context)
        } else {
            ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_SETTINGS
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun getSystemBrightness(context: Context): Int {
        return if (checkSystemWritePermission(context))
            Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    maxSystemBrightness)
        else 0
    }

    private fun setSystemBrightness(context: Context, newBrightness: Int) {
        if (checkSystemWritePermission(context)) {
            Settings.System.putInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    newBrightness
            )
        }
    }

    fun getActivityBrightness(activity: Activity): Float {
        val window = activity.window
        val layoutParams = window.attributes
        val brightnessPercent = layoutParams.screenBrightness
        if (brightnessPercent == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
            return WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF
        }
        return brightnessPercent
    }

    fun setAppBrightness(brightnessPercent: Float, activity: Activity) {
        val window = activity.window
        val layoutParams = window.attributes
        layoutParams.screenBrightness = brightnessPercent.toFloat()
        window.attributes = layoutParams
    }
}