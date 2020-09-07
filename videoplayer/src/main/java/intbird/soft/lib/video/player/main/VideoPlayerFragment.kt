package intbird.soft.lib.video.player.main

import android.os.Bundle
import android.view.View
import intbird.soft.lib.video.player.api.bean.MediaClarity
import intbird.soft.lib.video.player.api.bean.MediaPlayItem
import intbird.soft.lib.video.player.api.state.IVideoPlayerCallback
import intbird.soft.lib.video.player.api.state.IVideoPlayerController
import intbird.soft.lib.video.player.api.state.IVideoPlayerStateInfo
import intbird.soft.lib.video.player.api.style.MediaPlayerStyle
import intbird.soft.lib.video.player.main.player.call.IPlayerCallback
import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import intbird.soft.lib.video.player.main.player.state.IPlayerExecute

/**
 * created by intbird
 * on 2020/9/1
 * DingTalk id: intbird
 *
 * 时间有限
 */
class VideoPlayerFragment : VideoPlayerFragmentLite(), IPlayerExecute {

    companion object {
        fun newInstance(
            playList: ArrayList<MediaPlayItem>?,
            playIndex: Int,
            playerStyle: MediaPlayerStyle
        ): VideoPlayerFragment {
            return VideoPlayerFragmentLite.newInstance(playList, playIndex, playerStyle)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        states?.addCallback(playerCallback)
    }

    //---- 外部控制命令 start ----
    fun setVideoPlayerList(
        playList: ArrayList<MediaPlayItem>?,
        playIndex: Int,
        autoPlay: Boolean = false
    ) {
        if (isFinishing()) return
        setVideoPlayerItems(playList, playIndex)
        if (autoPlay) {
            play(PlayFlag.SELF)
        }
        log("setVideoPlayerList: playList:$playList playIndex: $playIndex autoPlay: $autoPlay")
    }

    var mediaStateCallback: IVideoPlayerCallback? = null

    override fun setPlayerStateCallback(playerCallback: IVideoPlayerCallback) {
        this.mediaStateCallback = playerCallback
    }

    override fun getVideoPlayerController(): IVideoPlayerController? {
        if (isFinishing()) return null
        return videoPlayerController
    }

    override fun getVideoPlayerStateInfo(): IVideoPlayerStateInfo? {
        if (isFinishing()) return null
        return videoPlayerStateInfo
    }

    private val playerCallback = object : IPlayerCallback {
        override fun onPrepare(mediaFileInfo: MediaFileInfo) {
            if (isFinishing()) return
            mediaStateCallback?.onPrepare()
        }

        override fun onPrepared(mediaFileInfo: MediaFileInfo) {
            if (isFinishing()) return
            mediaStateCallback?.onPrepared()
        }

        override fun onStart() {
            if (isFinishing()) return
            mediaStateCallback?.onStart()
        }

        override fun onSeekTo(duration: Long) {
            if (isFinishing()) return
            mediaStateCallback?.onSeekTo(duration)
        }

        override fun onPause() {
            if (isFinishing()) return
            mediaStateCallback?.onPause(player?.getCurrentTime())
        }

        override fun onCompletion(mediaFileInfo: MediaFileInfo) {
            if (isFinishing()) return
            mediaStateCallback?.onCompletion()
        }

        override fun onStop() {
            if (isFinishing()) return
            mediaStateCallback?.onStop()
        }

        override fun onError(errorMessage: String?) {
            if (isFinishing()) return
            mediaStateCallback?.onError(errorMessage)
        }

        override fun onVideoSizeChanged(mediaFileInfo: MediaFileInfo) {
            if (isFinishing()) return
        }

        override fun onBuffStart() {
            if (isFinishing()) return
        }

        override fun onBuffEnded() {
            if (isFinishing()) return
        }
    }

    private val videoPlayerController = object : IVideoPlayerController {
        override fun start() {
            if (isFinishing()) return
            player?.start()
        }

        override fun seekTo(duration: Long, autoPlay: Boolean) {
            if (isFinishing()) return
            player?.seekTo(duration, autoPlay)
        }

        override fun pause() {
            if (isFinishing()) return
            player?.pause()
        }

        override fun stop() {
            if (isFinishing()) return
            player?.stop()
        }

        override fun last() {
            if (isFinishing()) return
            play(PlayFlag.LAST)
        }

        override fun next() {
            if (isFinishing()) return
            play(PlayFlag.NEXT)
        }
    }

    private val videoPlayerStateInfo = object : IVideoPlayerStateInfo {

        override fun getVideoPlayingItem(): MediaPlayItem? {
            if (isFinishing()) return null
            return mediaPlayingItem
        }

        override fun getVideoPlayingItemChild(): MediaClarity? {
            if (isFinishing()) return null
            return mediaPlayingItemChild
        }

        override fun getCurrentTime(): Long? {
            if (isFinishing()) return 0L
            return player?.getCurrentTime()
        }

        override fun getTotalTime(): Long? {
            if (isFinishing()) return 0L
            return player?.getTotalTime()
        }

        override fun isLocked(): Boolean? {
            if (isFinishing()) return false
            return locker?.isLocked()
        }
    }
    //---- end ----

}