package intbird.soft.lib.video.player.main.player.player;

import android.content.Context
import android.net.Uri
import android.view.TextureView
import android.view.View
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.hls.HlsManifest
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import intbird.soft.lib.video.player.main.player.IPlayer
import intbird.soft.lib.video.player.main.player.call.IPlayerCallback
import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import intbird.soft.lib.video.player.main.player.display.IDisplay

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 *
 * viewImpl: 暂时没时间view接口,直接用view实现
 */
class ExoPlayerImpl(
    private val playerView: TextureView,
    private val playerCallback: IPlayerCallback?
) : IPlayer, IDisplay {
    private val context = playerView.context
    private var player = SimpleExoPlayer.Builder(context).build()

    init {
        playerView.visibility = View.VISIBLE
        displayStateChange(true)
    }

    override fun displayStateChange(enableDisplay: Boolean) {
        if (enableDisplay) player.setVideoTextureView(playerView)
    }

    /**
     * https://exoplayer.dev/hls.html
     */
    override fun prepare(mediaFileInfo: MediaFileInfo) {
        try {
            // Create a data source factory.
            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, "")
            )
            // Create a HLS media source pointing to a playlist uri.
            val hlsMediaSource =
                HlsMediaSource.Factory(dataSourceFactory)
                    .setAllowChunklessPreparation(true)
                    .createMediaSource(Uri.parse(mediaFileInfo.mediaPath))

            // Prepare the player with the media source.
            player.prepare(hlsMediaSource)

            player.playWhenReady = true
            player.addListener(object : Player.EventListener {
                override fun onLoadingChanged(isLoading: Boolean) {
                }

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    val manifest = player.currentManifest
                    if (manifest != null) {
                        val hlsManifest = manifest as HlsManifest
                        // Do something with the manifest.
                    }
                }
            })

        } catch (e: Exception) {
        }
    }

    override fun start() {
        player.playbackState
    }

    override fun seekTo(duration: Long, autoPlay: Boolean) {
        player.seekTo(duration)
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun last(): Boolean {
        return false
    }

    override fun next(): Boolean {
        return false
    }

    override fun stop() {
        player.stop()
    }

    override fun destroy() {
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
}