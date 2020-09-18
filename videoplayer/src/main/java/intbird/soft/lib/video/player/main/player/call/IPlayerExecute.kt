package intbird.soft.lib.video.player.main.player.call

import intbird.soft.lib.video.player.api.bean.MediaPlayItem
import intbird.soft.lib.video.player.api.bean.MediaPlayItemInfo
import intbird.soft.lib.video.player.api.state.IVideoPlayerCallback
import intbird.soft.lib.video.player.api.state.IVideoPlayerController
import intbird.soft.lib.video.player.api.state.IVideoPlayerStateInfo

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IPlayerExecute {
    fun registerPlayStateCallback(playerCallback: IVideoPlayerCallback)

    fun setVideoPlayerList(playList: ArrayList<MediaPlayItem>?, playIndex: Int, autoPlay:Boolean = true)
    fun setVideoPlayerItem(mediaPlayItem: MediaPlayItem?, autoPlay:Boolean = true)
    fun setVideoPlayerItemInfo(mediaPlayItemInfo: MediaPlayItemInfo?, autoPlay:Boolean = true)
}