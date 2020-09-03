package intbird.soft.lib.video.player.api.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MediaPlayItem(
    val mediaId: String = "",
    var mediaName: String? = "",
    var mediaConfig: ArrayList<MediaClarity>,

    val defaultSelected: Int? = 0,
    var defaultProgress: Long? = 0
) : Parcelable

@Parcelize
data class MediaClarity(
    val clarityIndex: Int,
    val mediaId: String,
    val clarityText: String,
    val mediaUrl: String,
    val mediaHeaders: Map<String, String>? = null,

    var clarityChecked: Boolean = false,
    var clarityProgress: Long = 0,
    var selectedByUser: Boolean = false
) : Parcelable