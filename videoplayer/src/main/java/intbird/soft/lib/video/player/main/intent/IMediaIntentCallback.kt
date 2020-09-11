package intbird.soft.lib.video.player.main.intent

import intbird.soft.lib.video.player.api.bean.MediaClarity
import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo

/**
 * created by intbird
 * on 2020/9/10
 * DingTalk id: intbird
 */

interface IMediaIntentCallback {
    fun onReceivePlayFile(mediaFileInfo: MediaFileInfo)

    // need recode
    fun getLastCheckedPlay(): MediaClarity?
    fun onReceivePlaylist(playlist: ArrayList<MediaClarity>?)
    fun onReceivePlayItem(playItem: MediaClarity?)
}