package intbird.soft.lib.video.player.api.state

import intbird.soft.lib.video.player.api.bean.MediaClarity
import intbird.soft.lib.video.player.api.bean.MediaPlayItem
import intbird.soft.lib.video.player.api.bean.MediaRate

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IVideoPlayerStateInfo {

    fun getVideoPlayingItem(): MediaPlayItem?

    fun getVideoPlayingItemClarity(): MediaClarity?

    fun getVideoPlayingItemRate(): MediaRate?

    fun getCurrentTime(): Long?

    fun getTotalTime(): Long?

    fun isLocked():Boolean?
}