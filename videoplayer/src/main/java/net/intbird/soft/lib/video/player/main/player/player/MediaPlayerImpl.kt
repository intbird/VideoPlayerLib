package net.intbird.soft.lib.video.player.main.player.player;

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.view.Surface
import android.view.TextureView
import android.widget.TextView
import net.intbird.soft.lib.video.player.api.error.MediaError
import net.intbird.soft.lib.video.player.main.player.IPlayer
import net.intbird.soft.lib.video.player.main.player.call.PlayerCallbacks
import net.intbird.soft.lib.video.player.main.player.display.subtitle.MediaPlayerSubtitle
import net.intbird.soft.lib.video.player.main.player.display.surface.IDisplay
import net.intbird.soft.lib.video.player.main.player.display.surface.TextureDisplay
import net.intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import net.intbird.soft.lib.video.player.main.intent.delegate.PlayerListDelegate
import net.intbird.soft.lib.video.player.utils.MediaLogUtil
import net.intbird.soft.lib.video.player.utils.MediaTimeUtil.adjustValueBoundL

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 *
 * viewImpl: 暂时没时间view接口,直接用view实现
 */
class MediaPlayerImpl(
    private val context: Context,
    private val display: TextureView?,
    private val subtitle: TextView,
    private val playerListDelegate: PlayerListDelegate?,
    private val playerCallback: PlayerCallbacks?
) :
    IPlayer, IDisplay,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnVideoSizeChangedListener {

    private var mediaPlayer: MediaPlayer? = null
    private var mediaDisplay: Surface? = null
    private var mediaSubtitle: MediaPlayerSubtitle? = null
    private var mediaPrepared = false
    private var mediaCompleted = false

    private val playerEnable
        get() = mediaDisplay != null && mediaPrepared

    private var mediaFileInfo: MediaFileInfo =
        MediaFileInfo()

    private var payingStateOnPause = false

    init {
        display?.surfaceTextureListener = TextureDisplay(this)
        mediaSubtitle = MediaPlayerSubtitle(context, subtitle)
    }

    private fun createMediaPlayer() {
        if (null != mediaPlayer) {
            return
        }
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer?.setScreenOnWhilePlaying(true)
            mediaPlayer?.isLooping = false

            mediaPlayer?.setOnVideoSizeChangedListener(this)
            mediaPlayer?.setOnCompletionListener(this)
            mediaPlayer?.setOnPreparedListener(this)
            mediaPlayer?.setOnInfoListener { _, what, _ ->
                log("setOnInfoListener: $what")
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) playerCallback?.onBuffStart()
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) playerCallback?.onBuffEnded()
                false
            }
            mediaPlayer?.setOnErrorListener { _, what, extra ->
                log("setOnErrorListener: $what $extra")
                playerCallback?.onError(MediaError.PLAYER_ERROR_CALLBACK,"what:$what extra:$extra")
                true
            }
            mediaSubtitle?.attachMediaPlayer(mediaPlayer)
        } catch (ignored: Exception) {
            log("init-error: ${ignored.message}")
            playerCallback?.onError(MediaError.PLAYER_INIT_ERROR,ignored.message)
            prepareReset()
        }
    }

    override fun displayStateChange(enableDisplay: Boolean) {
        if (null != display) mediaDisplay = Surface(display.surfaceTexture)
        createMediaPlayer()
        mediaPlayer?.setSurface(mediaDisplay)
        log("displayStateChange $mediaPrepared $playerEnable")
        playerCallback?.onReady(mediaFileInfo, playerEnable)
    }

    /**
     * params有空做下调整,和file分开或者将url,progres等一些信息进行完全挂载
     * 如: onReady() 进行的一些逻辑需要的参数等
     */
    override fun onParamsChange(mediaFileInfo: MediaFileInfo?) {
        if (!playerEnable) return
        if (null ==  mediaFileInfo) return

        // rate
        val mediaRate = mediaFileInfo.speedRate
        if(null != mediaRate && mediaRate > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer?.playbackParams = PlaybackParams().setSpeed(mediaRate)
            }
        }

        //subtitle
        mediaSubtitle?.onReceiveSubtitle(mediaFileInfo.subtitle)
    }

    override fun prepare(mediaFile: MediaFileInfo) {
        mediaFileInfo = mediaFile
        playerCallback?.onPrepare(mediaFile)
        createMediaPlayer()

        prepareReset()
        log("reset")

        try {
            mediaPlayer?.setDataSource(
                context,
                Uri.parse(mediaFileInfo.mediaUrl),
                mediaFileInfo.mediaHeaders
            )
            log("prepare")
            mediaPlayer?.prepareAsync()

        } catch (ignored: Exception) {
            log("prepare-error: ${ignored.message}")
            playerCallback?.onError(MediaError.PLAYER_PREPARE_ERROR, ignored.message)
            prepareReset()
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPrepared = true
        playerCallback?.onPrepared(mediaFileInfo)
        log("onPrepared")
        playerCallback?.onReady(mediaFileInfo, playerEnable)
        onParamsChange(mediaFileInfo)
    }

    private fun prepareReset() {
        mediaPrepared = false
        mediaPlayer?.reset()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (!mediaCompleted) {
            mediaCompleted = true
            mediaPlayer?.seekTo(getTotalTime().toInt())
            playerCallback?.onCompletion(mediaFileInfo)
        } else {
            playerCallback?.onCompletion(mediaFileInfo)
        }
        log("onCompletion: $mediaCompleted")
    }

    override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
        //if (!playerEnable) return // 去掉可用性检查
        mediaFileInfo.width = width
        mediaFileInfo.height = height
        playerCallback?.onVideoSizeChanged(mediaFileInfo)
        log("onVideoSizeChanged: width:$width  height:$height")
    }

    override fun start() {
        if (!playerEnable) return
        if (mediaPlayer?.isPlaying == true) return
        if (mediaCompleted) {
            mediaCompleted = false
            mediaPlayer?.seekTo(0)
        }
        mediaPlayer?.start()
        playerCallback?.onStart()
        log("start")
    }

    override fun seekTo(duration: Long, autoPlay: Boolean) {
        if (!playerEnable) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaPlayer?.seekTo(duration, MediaPlayer.SEEK_CLOSEST)
        } else {
            mediaPlayer?.seekTo(duration.toInt())
        }
        playerCallback?.onSeekTo(duration)
        mediaCompleted = (duration >= getTotalTime())
        if (autoPlay && !mediaCompleted) {
            mediaPlayer?.start()
            playerCallback?.onStart()
        }
        log("seekTo:$duration")
    }

    override fun resume() {
        if (!playerEnable) return
        seekTo(mediaPlayer?.currentPosition?.toLong() ?: 0, payingStateOnPause)
        log("resume")
    }

    override fun pause() {
        if (!playerEnable) return
        payingStateOnPause = mediaPlayer?.isPlaying ?: false
        if (!payingStateOnPause) return
        mediaPlayer?.pause()
        playerCallback?.onPause()
        log("pause")
    }

    override fun last():Boolean {
        return playerListDelegate?.delegateLast() == true
    }

    override fun next():Boolean {
       return playerListDelegate?.delegateNext() == true
    }

    override fun stop() {
        if (!playerEnable) return
        mediaPlayer?.stop()
        playerCallback?.onStop()
        log("stop")
    }

    override fun destroy() {
        mediaPlayer?.release()
        log("destroy")
    }

    override fun isPlaying(): Boolean {
        return if (playerEnable) mediaPlayer?.isPlaying ?: false else false
    }

    override fun getCurrentTime(): Long {
        return if (playerEnable) adjustValueBoundL(
            (mediaPlayer?.currentPosition?.toLong() ?: 0L),
            (mediaPlayer?.duration?.toLong() ?: 0L)
        ) else 0L
    }

    override fun getTotalTime(): Long {
        return if (playerEnable) mediaPlayer?.duration?.toLong() ?: 0L else 0L
    }

    private fun log(message: String) {
        MediaLogUtil.log("PlayerImpl $message  $mediaFileInfo")
    }
}