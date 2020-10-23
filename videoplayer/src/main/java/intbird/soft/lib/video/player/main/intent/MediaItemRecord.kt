package intbird.soft.lib.video.player.main.intent

import android.text.TextUtils

data class MediaRecordData(var lastProgress: Long?)

/**
 * only cache the last
 */
class MediaItemInfoRecord {
    private var lastMediaId: String? = ""
    private var lastMediaIdData: MediaRecordData? = null

    fun save(mediaId: String?, mediaIdData: MediaRecordData) {
        this.lastMediaId = mediaId
        this.lastMediaIdData = mediaIdData
    }

    fun get(mediaId: String?): MediaRecordData? {
        return if (null != mediaId
            && null != lastMediaId
            && TextUtils.equals(mediaId, lastMediaId)
        ) {
            lastMediaIdData
        } else null
    }
}