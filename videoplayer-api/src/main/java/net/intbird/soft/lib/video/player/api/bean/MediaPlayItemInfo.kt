package net.intbird.soft.lib.video.player.api.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * created by intbird
 * on 2020/9/8
 * DingTalk id: intbird
 */
@Parcelize
open class MediaPlayItemInfo(
    val mediaId: String? = "",
    val mediaName: String? = "",

    var mediaClarity: MediaClarity? = null,
    var mediaRate: MediaRate? = null,
    var mediaText: MediaText? = null
) : Parcelable {
    override fun toString(): String {
        return "MediaPlayItemInfo(mediaId='$mediaId', mediaName=$mediaName, mediaClarity=$mediaClarity, mediaRate=$mediaRate, mediaText=$mediaText)"
    }
}