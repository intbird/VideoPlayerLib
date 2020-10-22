package intbird.soft.lib.video.player.main.player.player;

import android.content.Context
import android.text.TextUtils
import android.view.Surface
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.hls.HlsManifest
import com.google.android.exoplayer2.ui.PlayerView
import intbird.soft.lib.video.player.api.error.MediaError
import intbird.soft.lib.video.player.main.player.IPlayer
import intbird.soft.lib.video.player.main.player.call.IPlayerCallback
import intbird.soft.lib.video.player.main.player.intent.delegate.PlayerDelegate
import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import intbird.soft.lib.video.player.utils.MediaLogUtil

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 *
 * viewImpl: 暂时没时间view接口,直接用view实现
 */
class ExoPlayerImpl(
    private val context: Context,
    private val playerView: PlayerView?,
    private val playerDelegate: PlayerDelegate?,
    private val playerCallback: IPlayerCallback?
) : IPlayer {
    private var player = SimpleExoPlayer.Builder(context).build()
    private var mediaFileInfo: MediaFileInfo? = null

    init {
        playerView?.player = player
    }

    private val playerStateListener = object : Player.EventListener {
        override fun onLoadingChanged(isLoading: Boolean) {
            if (isLoading) playerCallback?.onBuffStart() else playerCallback?.onBuffEnded()
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            log("onTimelineChanged ${timeline.periodCount}")
            val manifest = player?.currentManifest
            if (manifest != null) {
                val hlsManifest = manifest as? HlsManifest
                // Do something with the manifest.
            }
        }


        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)
            log("onPlaybackStateChanged $state")
            when (state) {
                Player.STATE_IDLE -> {
                    playerCallback?.onReady(mediaFileInfo!!, false)
                }
                Player.STATE_BUFFERING -> {
                    playerCallback?.onBuffStart()
                }
                Player.STATE_READY -> {
                    playerCallback?.onBuffEnded()
                }
                Player.STATE_ENDED -> {
                    playerCallback?.onCompletion(mediaFileInfo)
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            log("onIsPlayingChanged $isPlaying")
            if (isPlaying) playerCallback?.onStart() else playerCallback?.onPause()
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            log("onPlayerError $error")
            playerCallback?.onError(MediaError.PLAYER_EXO_ERROR, error.message)
        }
    }

    /**
     * https://codelabs.developers.google.com/codelabs/exoplayer-intro/#5  #Go deeper
     */
    private val playerAnalyticsListener = object : AnalyticsListener {

        override fun onRenderedFirstFrame(
            eventTime: AnalyticsListener.EventTime,
            surface: Surface?
        ) {
            super.onRenderedFirstFrame(eventTime, surface)
        }

        override fun onDroppedVideoFrames(
            eventTime: AnalyticsListener.EventTime,
            droppedFrames: Int,
            elapsedMs: Long
        ) {
            super.onDroppedVideoFrames(eventTime, droppedFrames, elapsedMs)
        }

        override fun onAudioUnderrun(
            eventTime: AnalyticsListener.EventTime,
            bufferSize: Int,
            bufferSizeMs: Long,
            elapsedSinceLastFeedMs: Long
        ) {
            super.onAudioUnderrun(eventTime, bufferSize, bufferSizeMs, elapsedSinceLastFeedMs)
        }
    }

    override fun onParamsChange(mediaFileInfo: MediaFileInfo?) {
        if (null == mediaFileInfo) return
        mediaFileInfo.speedRate?.run { player.setPlaybackParameters(PlaybackParameters(this)) }
    }

    override fun prepare(mediaFileInfo: MediaFileInfo) {
        if (TextUtils.isEmpty(mediaFileInfo.mediaUrl)) {
            playerCallback?.onError(MediaError.PLAYER_EXO_EMPTY, "empty url")
            return
        }
        this.mediaFileInfo = mediaFileInfo
        prepareMediaResource(mediaFileInfo.mediaUrl ?: "", mediaFileInfo.mediaHeaders)
    }

    private fun prepareMediaResource(uri: String?, headers: Map<String, String>?) {
        val mediaItem: MediaItem = MediaItem.Builder()
            .setUri(uri)
            .setDrmLicenseRequestHeaders(headers)
            .build()
        player?.setMediaItem(mediaItem)
        player?.playWhenReady = true
        player?.addListener(playerStateListener)
        player?.addAnalyticsListener(playerAnalyticsListener)
        player?.prepare()
    }

    override fun start() {
        player.playWhenReady = true
    }

    override fun seekTo(duration: Long, autoPlay: Boolean) {
        log("seekTo:$duration")
        player.seekTo(duration)
        player.playWhenReady = autoPlay
    }

    override fun resume() {
        player.playWhenReady = true
    }

    override fun pause() {
        player.playWhenReady = false
    }

    override fun last(): Boolean {
        return playerDelegate?.delegateLast() == true
    }

    override fun next(): Boolean {
        return playerDelegate?.delegateNext() == true
    }

    override fun stop() {
        player.stop()
    }

    override fun destroy() {
        player.removeAnalyticsListener(playerAnalyticsListener)
        player.removeListener(playerStateListener)
        player.release()
    }

    override fun isPlaying(): Boolean {
        return player.isPlaying
    }

    override fun getCurrentTime(): Long {
        return player.currentPosition
    }

    override fun getTotalTime(): Long {
        return player.totalBufferedDuration
    }

    private fun log(message: String) {
        MediaLogUtil.log("ExoplayerImpl: $message")
    }
}