package intbird.soft.lib.video.player.main.player.intent.parser

import android.text.TextUtils
import intbird.soft.lib.video.player.api.bean.*
import intbird.soft.lib.video.player.utils.MediaLogUtil

/**
 * created by intbird
 * on 2020/9/10
 * DingTalk id: intbird
 *
 * need recode this parser
 */
class MediaItemInfoParer {
    var playingItem: MediaPlayItem? = null
    var playingItemInfo: MediaPlayItemInfo? = null

    fun parserPlayItemInfo(playItem: MediaPlayItem?): MediaPlayItemInfo? {
        if (null == playItem) return null
        playingItem = playItem
        playingItemInfo = MediaPlayItemInfo(
            playingItem?.mediaId,
            playingItem?.mediaName,
            getPlayerMediaClarityItem(playItem),
            getPlayerMediaRateItem(playItem),
            getPlayerMediaTextItem(playItem)
        )
        return playingItemInfo
    }

    fun parserPlayItemInfo(mediaPlayItemInfo: MediaPlayItemInfo?): MediaPlayItemInfo? {
        if (null == mediaPlayItemInfo) return null
        playingItemInfo = mediaPlayItemInfo
        return playingItemInfo
    }

    fun changeSelectedClarity(mediaClarity: MediaClarity) {
        this.playingItemInfo?.mediaClarity = mediaClarity
    }

    fun changeSelectedRate(mediaRate: MediaRate) {
        this.playingItemInfo?.mediaRate = mediaRate
    }

    fun changeSelectedText(mediaText: MediaText?) {
        this.playingItemInfo?.mediaText = mediaText
    }

    private fun getPlayerMediaClarityItem(playItem: MediaPlayItem): MediaClarity? {
        val list = playItem.clarityArray ?: ArrayList()
        val index = getSelectedClarity(playItem.mediaId, list)
        log("get Clarity:$index")
        val size = list.size
        return if (index in 0 until size) {
            list[index]
        } else null
    }

    private fun getSelectedClarity(mediaId: String?, list: ArrayList<MediaClarity>): Int {
        return if (null != this.playingItemInfo
            && null != playingItemInfo?.mediaClarity
            && TextUtils.equals(
                this.playingItemInfo?.mediaId,
                mediaId
            )
        ) {
            val index = list.indexOf(playingItemInfo!!.mediaClarity)
            return if (-1 != index) index else 0
        } else {
            for (item in list) {
                val index = list.indexOf(item)
                return if (item.checked) index else continue
            };0
        }
    }

    private fun getPlayerMediaRateItem(playItem: MediaPlayItem): MediaRate? {
        val list = playItem.rateArray ?: ArrayList()
        val index = getSelectRate(playItem.mediaId, list)
        val size = list.size
        log("get Rate:$index")
        return if (index in 0 until size) {
            list[index]
        } else null
    }

    private fun getSelectRate(mediaId: String?, list: ArrayList<MediaRate>): Int {
        return if (null != this.playingItemInfo
            && null != playingItemInfo?.mediaRate
            && TextUtils.equals(
                this.playingItemInfo?.mediaId,
                mediaId
            )
        ) {
            val index = list.indexOf(playingItemInfo!!.mediaRate)
            return if (-1 != index) index else 0
        } else {
            for (item in list) {
                val index = list.indexOf(item)
                return if (item.checked) index else continue
            };0
        }
    }

    private fun getPlayerMediaTextItem(playItem: MediaPlayItem): MediaText? {
        val list = playItem.textArray ?: ArrayList()
        val index = getSelectText(playItem.mediaId, list)
        val size = list.size
        log("get Text:$index")
        return if (index in 0 until size) {
            list[index]
        } else null
    }

    private fun getSelectText(mediaId: String?, list: ArrayList<MediaText>): Int {
        return if (null != this.playingItemInfo
            && null != playingItemInfo?.mediaText
            && TextUtils.equals(
                this.playingItemInfo?.mediaId,
                mediaId
            )
        ) {
            val index = list.indexOf(playingItemInfo!!.mediaText)
            return if (-1 != index) index else 0
        } else {
            for (item in list) {
                val index = list.indexOf(item)
                return if (item.checked) index else continue
            };0
        }
    }

    private fun log(message: String) {
        MediaLogUtil.log("intents: $message")
    }
}