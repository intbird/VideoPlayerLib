package intbird.soft.lib.video.player.main.player.call

import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IPlayerCallback {
    fun onPrepared(mediaFileInfo: MediaFileInfo)

    fun onStart()

    fun onSeekTo(duration: Long)

    fun onPause()

    fun onCompletion(mediaFileInfo: MediaFileInfo)

    fun onStop()

    fun onVideoSizeChanged(mediaFileInfo: MediaFileInfo)
}