package intbird.soft.lib.video.player.api.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MediaPlayItem(
    val mediaId: String = "",
    var mediaName: String? = "",

    var clarityArray: ArrayList<MediaClarity>,
    var rateArray: ArrayList<MediaRate>,

    var defaultProgress: Long? = 0
) : Parcelable

@Parcelize
data class MediaClarity(
    val clarity: String,
    val mediaUrl: String,
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
    val rate: Float
) : MediaCheckedData(text = rate.toString()),
    Parcelable {
    fun checked(): MediaRate {
        this.checked = true
        return this
    }
}

sealed class MediaCheckedData(
    val text: String = "",
    open var checked: Boolean = false
)