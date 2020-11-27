package net.intbird.soft.lib.video.player.main.player.player;

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.TextUtils
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.text.CaptionStyleCompat
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.MimeTypes
import net.intbird.soft.lib.video.player.api.error.MediaError
import net.intbird.soft.lib.video.player.main.intent.delegate.PlayerListDelegate
import net.intbird.soft.lib.video.player.main.player.IPlayer
import net.intbird.soft.lib.video.player.main.player.call.IPlayerCallback
import net.intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import net.intbird.soft.lib.video.player.main.player.player.event.ExoPlayerStateHandler
import net.intbird.soft.lib.video.player.utils.MediaLogUtil
import java.io.File

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
    private val playerListDelegate: PlayerListDelegate?,
    private val playerCallback: IPlayerCallback?
) : IPlayer {
    private var player: SimpleExoPlayer? = null
    private lateinit var playerEvent: ExoPlayerStateHandler

    private var mediaFileInfo: MediaFileInfo? = null

    init {
        initSimpleExoPlayer()
        player?.addAnalyticsListener(EventLogger(DefaultTrackSelector(context)))
    }

    private fun initSimpleExoPlayer() {
        val httpDataSourceFactory: HttpDataSource.Factory = DefaultHttpDataSourceFactory(
            ExoPlayerLibraryInfo.DEFAULT_USER_AGENT,
            DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
            DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,  /* allowCrossProtocolRedirects= */
            true
        )
        val cacheDataSource = CacheDataSource.Factory()
            .setCache(PlayerInstance.getSimpleLeastCache(context))
            .setUpstreamDataSourceFactory(httpDataSourceFactory)

        val dataSourceFactory = DefaultDataSourceFactory(context, cacheDataSource)
        player = SimpleExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()
        playerEvent = ExoPlayerStateHandler(player, playerCallback)

        playerView?.subtitleView?.setFixedTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 16f)
        playerView?.subtitleView?.setStyle(
            CaptionStyleCompat(
                Color.WHITE, Color.TRANSPARENT, Color.parseColor("#70000000"),
                CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                Color.TRANSPARENT,
                Typeface.DEFAULT
            )
        )
        playerView?.player = player
    }

    override fun onParamsChange(mediaFileInfo: MediaFileInfo?) {
        if (null == mediaFileInfo) return
        mediaFileInfo.speedRate?.run { player?.setPlaybackParameters(PlaybackParameters(this)) }

        // 重新选择字幕
        //player?.trackSelector.selectTracks()
    }

    override fun prepare(mediaFileInfo: MediaFileInfo) {
        if (TextUtils.isEmpty(mediaFileInfo.mediaUrl)) {
            playerCallback?.onError(MediaError.PLAYER_EXO_EMPTY, "empty url")
            return
        }
        this.mediaFileInfo = mediaFileInfo
        this.playerEvent.mediaFileInfo = mediaFileInfo
        prepareMediaResource(
            mediaFileInfo.mediaUrl ?: "",
            mediaFileInfo.mediaHeaders,
            mediaFileInfo.subtitle
        )
        mediaFileInfo.speedRate?.run { player?.setPlaybackParameters(PlaybackParameters(this)) }
    }

    private fun prepareMediaResource(
        uri: String?,
        headers: Map<String, String>?,
        subtitlePath: String?
    ) {
        val mediaItem: MediaItem = MediaItem.Builder()
            .setSubtitles(setSubTitlePath(subtitlePath))
            .setUri(uri)
            .build()
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
        playerEvent.register()
    }

    private fun setSubTitlePath(subtitlePath: String?): ArrayList<MediaItem.Subtitle> {
        val subtitleItem = when {
            subtitlePath?.endsWith(".vtt") == true -> {
                MediaItem.Subtitle(
                    Uri.parse(subtitlePath),
                    MimeTypes.TEXT_VTT,
                    null,
                    C.SELECTION_FLAG_FORCED
                )
            }
            subtitlePath?.endsWith(".ssa") == true -> {
                MediaItem.Subtitle(
                    Uri.parse(subtitlePath),
                    MimeTypes.TEXT_SSA,
                    null,
                    C.SELECTION_FLAG_FORCED
                )
            }
            subtitlePath?.endsWith(".srt") == true -> {
                MediaItem.Subtitle(
                    Uri.parse(subtitlePath),
                    MimeTypes.APPLICATION_SUBRIP,
                    null,
                    C.SELECTION_FLAG_FORCED
                )
            }
            else -> {
                MediaItem.Subtitle(
                    Uri.parse(subtitlePath),
                    MimeTypes.APPLICATION_SUBRIP,
                    null,
                    C.SELECTION_FLAG_FORCED
                )
            }
        }
        return arrayListOf(subtitleItem)
    }

    override fun start() {
        player?.playWhenReady = true
    }

    override fun seekTo(duration: Long, autoPlay: Boolean) {
        log("seekTo:$duration")
        player?.seekTo(duration)
        player?.playWhenReady = autoPlay
    }

    override fun resume() {
        player?.playWhenReady = true
    }

    override fun pause() {
        player?.playWhenReady = false
    }

    override fun last(): Boolean {
        return playerListDelegate?.delegateLast() == true
    }

    override fun next(): Boolean {
        return playerListDelegate?.delegateNext() == true
    }

    override fun stop() {
        player?.stop()
    }

    override fun destroy() {
        playerEvent.unregister()
        player?.release()
    }

    override fun isPlaying(): Boolean {
        return player?.isPlaying == true
    }

    override fun getCurrentTime(): Long {
        return player?.currentPosition ?: 0
    }

    override fun getTotalTime(): Long {
        return player?.duration ?: 0
    }

    private fun log(message: String) {
        MediaLogUtil.log("ExoplayerImpl: $message")
    }

    object PlayerInstance {
        private var simpleCache: SimpleCache? = null

        fun getSimpleCache(context: Context): SimpleCache {
            if (null == simpleCache) {
                simpleCache = SimpleCache(
                    File(context.applicationContext.cacheDir, "exoplayer-temps"),
                    NoOpCacheEvictor(),
                    ExoDatabaseProvider(context)
                )
            }
            return simpleCache!!
        }

        fun getSimpleLeastCache(context: Context): SimpleCache {
            if (null == simpleCache) {
                simpleCache = SimpleCache(
                    File(context.applicationContext.cacheDir, "exoplayer-temps"),
                    LeastRecentlyUsedCacheEvictor(1 * 1024 * 1024),
                    ExoDatabaseProvider(context)
                )
            }
            return simpleCache!!
        }
    }
}