package intbird.soft.lib.video.player.main.player.call

import intbird.soft.lib.video.player.api.IVideoPlayerCallback
import intbird.soft.lib.video.player.api.bean.MediaPlayItem
import intbird.soft.lib.video.player.main.player.IPlayer

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IPlayerExecute {
    fun registerPlayStateCallback(playerState: IVideoPlayerCallback)

    fun setVideoPlayerList(playList: ArrayList<MediaPlayItem>?, playIndex: Int, autoPlay:Boolean = true)
    fun setVideoPlayerNext()
    fun setVideoPlayerLast()

    fun getVideoPlayerController():IPlayer?
}