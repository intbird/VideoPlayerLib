package net.intbird.soft.lib.video.player.api.const

import android.text.TextUtils
import net.intbird.soft.lib.video.player.api.bean.MediaClarity
import net.intbird.soft.lib.video.player.api.bean.MediaFileType
import net.intbird.soft.lib.video.player.api.bean.MediaPlayItem

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

    fun generalWebViewMediaItem(url: String) : MediaPlayItem {
       return MediaPlayItem(
            mediaId = url,
            mediaName = "",
            clarityArray = arrayListOf(MediaClarity("", "${MediaFileType.WEBSITE.type}${url}").checked()),
            rateArray = arrayListOf(),
            textArray = arrayListOf(),
            defaultProgress = 0
        )
    }
}
