package intbird.soft.lib.video.player.main.player;

import android.view.Surface
import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IPlayer {
    /**
     * 实际上这个通知由display调用,这里先简化一下
     */
    fun available(display: Surface?)

    fun prepare(mediaFileInfo: MediaFileInfo)

    fun start()

    fun seekTo(duration: Long, start: Boolean)

    fun resume()

    fun pause()

    fun stop()

    fun destroy()

    fun isPlaying(): Boolean

    fun getCurrentTime(): Long

    fun getTotalTime(): Long
}
