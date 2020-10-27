package net.intbird.soft.lib.video.player.api.state

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IVideoPlayerController {

    fun start()

    fun seekTo(duration: Long, autoPlay: Boolean)

    fun pause()

    fun stop()

    fun last()

    fun next()
}