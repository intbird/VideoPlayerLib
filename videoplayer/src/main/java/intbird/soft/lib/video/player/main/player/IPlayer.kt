package intbird.soft.lib.video.player.main.player;

import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IPlayer {

    fun prepare(mediaFileInfo: MediaFileInfo)

    fun start()

    fun seekTo(duration: Long, autoPlay: Boolean)

    fun resume()

    fun pause()

    fun stop()

    fun destroy()

    fun isPlaying(): Boolean

    fun getCurrentTime(): Long

    fun getTotalTime(): Long
}
