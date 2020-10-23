package intbird.soft.lib.video.player.main.intent.parser

import android.text.TextUtils
import androidx.annotation.NonNull
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
            getSelectedItem(playItem.mediaId, playItem.clarityArray, playingItemInfo?.mediaClarity) as? MediaClarity,
            getSelectedItem(playItem.mediaId, playItem.rateArray, playingItemInfo?.mediaRate) as? MediaRate,
            getSelectedItem(playItem.mediaId, playItem.textArray, playingItemInfo?.mediaText) as? MediaText
        )
        return playingItemInfo
    }

    fun changeSelectedData(mediaCheckedData: MediaCheckedData) {
        if (mediaCheckedData is MediaClarity) {
            this.playingItemInfo?.mediaClarity = mediaCheckedData
        }
        if (mediaCheckedData is MediaRate) {
            this.playingItemInfo?.mediaRate =  mediaCheckedData
        }
        if (mediaCheckedData is MediaText) {
            this.playingItemInfo?.mediaText = mediaCheckedData
        }
    }

    private fun getSelectedItem( id:String, arrayList: ArrayList<out MediaCheckedData>?, value: MediaCheckedData?): MediaCheckedData? {
        val nonNullArrayList = arrayList ?: ArrayList()
        val index = getSelectedItemIndex(nonNullArrayList, value, id)
        log("getSelectedItem:$index")
        val size = nonNullArrayList.size
        return if (index in 0 until size) nonNullArrayList[index] else null
    }

    private fun getSelectedItemIndex(@NonNull listData: ArrayList<out MediaCheckedData>, value: MediaCheckedData?, id:String): Int {
        val lastIndex = getLastSelectedItemIndex(listData, value, id)
        log("getLastSelectedItemIndex:$lastIndex")
        if (lastIndex != -1) return lastIndex
        val checkedIndex = getCheckSelectedItemIndex(listData)
        log("getCheckSelectedItemIndex:$checkedIndex")
        if (checkedIndex != -1) return checkedIndex
        return 0
    }

    private fun getLastSelectedItemIndex(@NonNull listData: ArrayList<out MediaCheckedData>, value: MediaCheckedData?, id:String): Int {
        if (null != this.playingItemInfo
            && null != value
            && TextUtils.equals(this.playingItemInfo?.mediaId, id)
        ) {
            return listData.indexOf(value)
        }
        return -1
    }

    private fun getCheckSelectedItemIndex(@NonNull listData: ArrayList<out MediaCheckedData>): Int {
        for (item in listData) {
            val index = listData.indexOf(item)
            return if (item.checked) index else continue
        }
        return -1
    }

    private fun log(message: String) {
        MediaLogUtil.log("intents: $message")
    }
}