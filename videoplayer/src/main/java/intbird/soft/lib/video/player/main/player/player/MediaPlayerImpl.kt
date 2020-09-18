package intbird.soft.lib.video.player.main.player.player;

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.view.Surface
import android.view.TextureView
import intbird.soft.lib.video.player.api.error.MediaError
import intbird.soft.lib.video.player.main.player.IPlayer
import intbird.soft.lib.video.player.main.player.call.PlayerCallbacks
import intbird.soft.lib.video.player.main.player.display.IDisplay
import intbird.soft.lib.video.player.main.player.display.TextureDisplay
import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import intbird.soft.lib.video.player.main.player.player.delegate.PlayerDelegate
import intbird.soft.lib.video.player.utils.MediaLogUtil
import intbird.soft.lib.video.player.utils.MediaTimeUtil.adjustValueBoundL

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 *
 * viewImpl: 暂时没时间view接口,直接用view实现
 */
class MediaPlayerImpl(
    private val context: Context,
    private val textureView: TextureView?,
    private val playerDelegate: PlayerDelegate?,
    private val playerCallback: PlayerCallbacks?
) :
    IPlayer, IDisplay,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnVideoSizeChangedListener {

    private var mediaPlayer: MediaPlayer? = null
    private var mediaDisplay: Surface? = null
    private var mediaPrepared = false
    private var mediaCompleted = false

    private val playerEnable
        get() = mediaDisplay != null && mediaPrepared

    private var mediaFileInfo: MediaFileInfo =
        MediaFileInfo()

    private var payingStateOnPause = false

    init {
        textureView?.surfaceTextureListener = TextureDisplay(this)
    }

    private fun createMediaPlayer() {
        if (null != mediaPlayer) {
            mediaPlayer?.setSurface(mediaDisplay)
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
            mediaPlayer?.setSurface(mediaDisplay)
        } catch (ignored: Exception) {
            log("init-error: ${ignored.message}")
            playerCallback?.onError(MediaError.PLAYER_INIT_ERROR,ignored.message)
            prepareReset()
        }
    }

    override fun displayStateChange(enableDisplay: Boolean) {
        if (null != textureView) mediaDisplay = Surface(textureView.surfaceTexture)
        createMediaPlayer()
        log("displayStateChange $mediaPrepared $playerEnable")
        playerCallback?.onReady(mediaFileInfo, playerEnable)
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
        return playerDelegate?.delegateLast() == true
    }

    override fun next():Boolean {
       return playerDelegate?.delegateNext() == true
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