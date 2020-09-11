package intbird.soft.lib.video.player.main

import android.os.Bundle
import android.view.View
import intbird.soft.lib.video.player.api.bean.MediaClarity
import intbird.soft.lib.video.player.api.bean.MediaPlayItem
import intbird.soft.lib.video.player.api.state.IVideoPlayerCallback
import intbird.soft.lib.video.player.api.state.IVideoPlayerController
import intbird.soft.lib.video.player.api.state.IVideoPlayerStateInfo
import intbird.soft.lib.video.player.main.player.call.IPlayerCallback
import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import intbird.soft.lib.video.player.main.player.state.IPlayerExecute
import intbird.soft.lib.video.player.main.view.MediaPlayerType

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
            playerStyle: MediaPlayerType,
            autoPlay: Boolean
        ): VideoPlayerFragment {
            return VideoPlayerFragmentLite.newInstance(playList, playIndex, playerStyle, autoPlay)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        states?.addCallback(playerCallback)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mediaStateCallback?.onCreated(this)
    }

    //---- 外部控制命令 start ----
    fun setVideoPlayerList(playList: ArrayList<MediaPlayItem>?, playIndex: Int, autoPlay:Boolean) {
        if (isFinishing()) return
        intentParser?.setVideoPlayerList(playList, playIndex, autoPlay)
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
            mediaStateCallback?.onPrepare()
        }

        override fun onPrepared(mediaFileInfo: MediaFileInfo) {
            mediaStateCallback?.onPrepared()
        }

        override fun onStart() {
            mediaStateCallback?.onStart()
        }

        override fun onSeekTo(duration: Long) {
            mediaStateCallback?.onSeekTo(duration)
        }

        override fun onPause() {
            mediaStateCallback?.onPause(player?.getCurrentTime())
        }

        override fun onCompletion(mediaFileInfo: MediaFileInfo) {
            mediaStateCallback?.onCompletion()
        }

        override fun onStop() {
            mediaStateCallback?.onStop()
        }

        override fun onError(errorCode:Int, errorMessage: String?) {
            mediaStateCallback?.onError(errorCode, errorMessage)
        }

        override fun onVideoSizeChanged(mediaFileInfo: MediaFileInfo) {
        }

        override fun onBuffStart() {
            mediaStateCallback?.onBuffStart()
        }

        override fun onBuffEnded() {
            mediaStateCallback?.onBuffEnded()
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
            player?.last()
        }

        override fun next() {
            if (isFinishing()) return
            player?.next()
        }
    }

    private val videoPlayerStateInfo = object : IVideoPlayerStateInfo {

        override fun getVideoPlayingItem(): MediaPlayItem? {
            if (isFinishing()) return null
            return intentParser?.getPlayingItem()
        }

        override fun getVideoPlayingItemChild(): MediaClarity? {
            if (isFinishing()) return null
            return intentParser?.getPlayingItemChild()
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