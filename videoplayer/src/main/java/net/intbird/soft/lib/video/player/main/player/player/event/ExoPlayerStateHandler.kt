package net.intbird.soft.lib.video.player.main.player.player.event

import android.view.Surface
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.hls.HlsManifest
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.video.VideoListener
import net.intbird.soft.lib.video.player.api.error.MediaError
import net.intbird.soft.lib.video.player.main.player.call.IPlayerCallback
import net.intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import net.intbird.soft.lib.video.player.utils.MediaLogUtil

class ExoPlayerStateHandler(val player: SimpleExoPlayer?, val playerCallback: IPlayerCallback?) {

    var mediaFileInfo: MediaFileInfo? = null

    fun register() {
        player?.addListener(playerStateListener) // onCreate -> onDestroy
        player?.addAnalyticsListener(playerAnalyticsListener)
        player?.addVideoListener(playerVideoListener)
        player?.addTextOutput(playerTextOutputListener)
    }

    fun unregister() {
        player?.removeListener(playerStateListener)
        player?.removeAnalyticsListener(playerAnalyticsListener)
        player?.removeVideoListener(playerVideoListener)
        player?.removeTextOutput(playerTextOutputListener)
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

        override fun onPositionDiscontinuity(reason: Int) {
            super.onPositionDiscontinuity(reason)
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


        override fun onVideoSizeChanged(
            eventTime: AnalyticsListener.EventTime,
            width: Int,
            height: Int,
            unappliedRotationDegrees: Int,
            pixelWidthHeightRatio: Float
        ) {
            super.onVideoSizeChanged(
                eventTime,
                width,
                height,
                unappliedRotationDegrees,
                pixelWidthHeightRatio
            )

            mediaFileInfo?.width = width
            mediaFileInfo?.height = height
            playerCallback?.onVideoSizeChanged(mediaFileInfo)
        }
    }


    private val playerVideoListener = object : VideoListener {

    }


    private val playerTextOutputListener = TextOutput {
        log("${it.size}")
    }

    private fun log(message: String) {
        MediaLogUtil.log("ExoPlayerStateHandler: $message")
    }
}