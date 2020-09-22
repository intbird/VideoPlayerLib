package intbird.soft.lib.video.player.api.const

import android.text.TextUtils

object MediaTextConfig {
    const val SHOW_ICON = "TIMED_TEXT_SHOW_ICON"
    const val HIDE_ICON = "TIMED_TEXT_HIDE_ICON"
}

object ConstConfigs {
    fun isVisible(text: String?): Boolean {
        return !(TextUtils.isEmpty(text) || TextUtils.equals(text, MediaTextConfig.HIDE_ICON))
    }

    fun getText(float: Float?): String {
        return if (null != float && float > 0f) float.toString()
        else ""
    }
}
