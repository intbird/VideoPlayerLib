package intbird.soft.lib.video.player.main.player.state

import intbird.soft.lib.video.player.api.state.IVideoPlayerCallback
import intbird.soft.lib.video.player.api.state.IVideoPlayerController
import intbird.soft.lib.video.player.api.state.IVideoPlayerStateInfo

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IPlayerExecute {
    fun setPlayerStateCallback(playerCallback: IVideoPlayerCallback)
    fun getVideoPlayerController(): IVideoPlayerController?
    fun getVideoPlayerStateInfo(): IVideoPlayerStateInfo?
}