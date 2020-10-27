package net.intbird.soft.lib.video.player.main.player.player;

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.MimeTypes
import net.intbird.soft.lib.video.player.api.error.MediaError
import net.intbird.soft.lib.video.player.main.intent.delegate.PlayerListDelegate
import net.intbird.soft.lib.video.player.main.player.IPlayer
import net.intbird.soft.lib.video.player.main.player.call.IPlayerCallback
import net.intbird.soft.lib.video.player.main.player.player.event.ExoPlayerStateHandler
import net.intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import net.intbird.soft.lib.video.player.utils.MediaLogUtil

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
    private var player = SimpleExoPlayer.Builder(context).build()
    private var playerEvent = ExoPlayerStateHandler(player, playerCallback)

    private var mediaFileInfo: MediaFileInfo? = null

    init {
        playerView?.player = player
        player?.addAnalyticsListener(EventLogger(DefaultTrackSelector(context)))
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
        this.playerEvent.mediaFileInfo = mediaFileInfo
        prepareMediaResource(mediaFileInfo.mediaUrl ?: "", mediaFileInfo.mediaHeaders , mediaFileInfo.subtitle)
        onParamsChange(mediaFileInfo)
    }

    private fun prepareMediaResource(uri: String?, headers: Map<String, String>?,subtitlePath: String?) {
        val mediaSubtitle = MediaItem.Subtitle(Uri.parse(subtitlePath?:""), MimeTypes.APPLICATION_SUBRIP,"")

        val mediaItem: MediaItem = MediaItem.Builder()
            .setUri(uri)
            .setDrmLicenseRequestHeaders(headers)
            .setSubtitles(arrayListOf(mediaSubtitle))
            .build()
        player?.setMediaItem(mediaItem)
        player?.playWhenReady = true
        playerEvent.register()
        player?.prepare()
    }

    override fun start() {
        player
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
        return playerListDelegate?.delegateLast() == true
    }

    override fun next(): Boolean {
        return playerListDelegate?.delegateNext() == true
    }

    override fun stop() {
        player.stop()
    }

    override fun destroy() {
        playerEvent.unregister()
        player.release()
    }

    override fun isPlaying(): Boolean {
        return player.isPlaying
    }

    override fun getCurrentTime(): Long {
        return player.currentPosition
    }

    override fun getTotalTime(): Long {
        return player.duration
    }

    private fun log(message: String) {
        MediaLogUtil.log("ExoplayerImpl: $message")
    }
}