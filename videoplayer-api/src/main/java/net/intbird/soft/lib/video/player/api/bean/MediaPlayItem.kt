package net.intbird.soft.lib.video.player.api.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * created by intbird
 * on 2020/9/8
 * DingTalk id: intbird
 */
@Parcelize
open class MediaPlayItem(
    val mediaId: String = "",
    var mediaName: String? = "",

    var clarityArray: ArrayList<MediaClarity>? = null,
    var rateArray: ArrayList<MediaRate>? = null,
    var textArray: ArrayList<MediaText>? = null,

    var defaultProgress: Long? = 0
) : Parcelable {
    override fun toString(): String {
        return "MediaPlayItem(mediaId='$mediaId', mediaName=$mediaName, clarityArray=$clarityArray, rateArray=$rateArray, textArray=$textArray, defaultProgress=$defaultProgress)"
    }
}

@Parcelize
data class MediaClarity(
    val clarity: String?,
    val mediaUrl: String?,
    val mediaHeaders: Map<String, String>? = null
) : MediaCheckedData(text = clarity),
    Parcelable {
    fun checked(): MediaClarity {
        this.checked = true
        return this
    }
}

@Parcelize
data class MediaRate(
    val name: String,
    val rate: Float
) : MediaCheckedData(text = name),
    Parcelable {
    fun checked(): MediaRate {
        this.checked = true
        return this
    }
}

@Parcelize
data class MediaText(
    val name: String,
    val path: String = ""
) : MediaCheckedData(text = name),
    Parcelable {
    fun checked(): MediaText {
        this.checked = true
        return this
    }
}

sealed class MediaCheckedData(
    val text: String? = "",
    open var checked: Boolean = false
)