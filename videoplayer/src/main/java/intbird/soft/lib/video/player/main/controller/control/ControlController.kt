package intbird.soft.lib.video.player.main.controller.control

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import kotlinx.android.synthetic.main.lib_media_player_control.view.*
import intbird.soft.lib.video.player.R
import intbird.soft.lib.video.player.main.notify.ILandscapeExecute
import intbird.soft.lib.video.player.main.controller.control.call.IControlCallback
import intbird.soft.lib.video.player.main.locker.call.ILockCallback
import intbird.soft.lib.video.player.main.notify.ILockExecute
import intbird.soft.lib.video.player.main.player.IPlayer
import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import intbird.soft.lib.video.player.utils.MediaTimeUtil

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 *
 * viewImpl: 暂时不写view接口,直接用view实现
 */
class ControlController(
        private val player: IPlayer?,
        private val lockCallback: ILockCallback?,
        private val iControlCallback: IControlCallback?,
        private val viewImpl: View,
        private val viewImplTitle: View
) : ILockExecute, ILandscapeExecute {
    private var handler = Handler(Looper.getMainLooper())
    private val progressInterval = 1000L
    private val dismissInterval = 3000L

    private val visibleDuration = 200L
    private val inVisibleDuration = 400L

    private val mediaTotalTime : Long
        get() = player?.getTotalTime() ?: 0L

    private val mediaCurrentTime : Long
        get() = player?.getCurrentTime() ?: 0L

    private val mProgressTicker: Runnable = object : Runnable {
        override fun run() {
            updateSeekProgressUI()
            handler.postDelayed(this, progressInterval)
        }
    }

    private val mDismissIndicator: Runnable = Runnable { toggleVisible(false) }

    private val onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener =
            object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    iControlCallback?.onBeforeDropSeek()
                }

                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    if (b) {
                        viewImpl.tvVideoCurTime.text = MediaTimeUtil.formatTime(i.toLong())
                        iControlCallback?.onDroppingSeek(i.toLong())
                    }
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    iControlCallback?.onAfterDropSeek()
                }
            }

    init {
        viewImpl.setOnClickListener { toggleVisible(true) }
        viewImpl.seekBarProgress.setOnSeekBarChangeListener(onSeekBarChangeListener)
        viewImpl.ivLast.setOnClickListener { iControlCallback?.last() }
        viewImpl.ivNext.setOnClickListener { iControlCallback?.next() }
        toggleLock(false)
        togglePlay(false)
        toggleDirection(false)
    }

    override fun executeLock(lock: Boolean) {
        toggleLock(lock)
    }

    override fun onLandscape() {
        toggleDirection(true)
    }

    override fun onPortrait() {
        toggleDirection(false)
    }

    fun onPrepared(mediaFileInfo: MediaFileInfo) {
        viewImplTitle.tvVideoName.text = mediaFileInfo.fileName ?: ""
        updateSeekProgressUI()
    }

    fun onStart() {
        togglePlay(true)
        listeningSeekTacking(true)
        listeningControlVisible(true)
    }

    fun onSeekTo(duration: Long) {
        updateSeekProgressUI()
    }

    fun onPause() {
        togglePlay(false)
        listeningSeekTacking(false)
        listeningControlVisible(false)
    }

    fun onCompletion() {
        updateCompleteUI()
    }

    fun onStop() {
        updateCompleteUI()
    }

    private fun updateCompleteUI() {
        toggleVisible(true)
        togglePlay(false)
        toggleLock(false)

        listeningSeekTacking(false)
        listeningControlVisible(false)
    }

    private fun updateSeekProgressUI() {
        if (null != player) {
            viewImpl.seekBarProgress.progress = mediaCurrentTime.toInt()
            viewImpl.seekBarProgress.max = mediaTotalTime.toInt()

            viewImpl.tvVideoCurTime.text = MediaTimeUtil.formatTime(mediaCurrentTime)
            viewImpl.tvVideoTotalTime.text = MediaTimeUtil.formatTime(mediaTotalTime)
        }
    }

    private fun togglePlay(play: Boolean) {
        updateSeekProgressUI()
        if (play) {
            viewImpl.ivPlay.setImageResource(R.drawable.icon_video_player_pause)
            viewImpl.ivPlay.setOnClickListener {
                togglePlay(false)
                player?.pause()
            }
        } else {
            viewImpl.ivPlay.setImageResource(R.drawable.icon_video_player_play)
            viewImpl.ivPlay.setOnClickListener {
                togglePlay(true)
                player?.start()
            }
        }
    }

    private fun toggleLock(lock: Boolean) {
        toggleVisible(!lock)
        if (lock) {
            viewImpl.ivLock.setImageResource(R.drawable.icon_video_player_lock)
            viewImpl.ivLock.setOnClickListener {
                lockCallback?.needUnLock(false)
            }
        } else {
            viewImpl.ivLock.setImageResource(R.drawable.icon_video_player_unlock)
            viewImpl.ivLock.setOnClickListener {
                lockCallback?.needLock()
            }
        }
    }

    private fun toggleDirection(large: Boolean) {
        val titleLp = viewImplTitle.tvVideoName.layoutParams as LinearLayout.LayoutParams
        titleLp.weight = viewImpl.context.resources.getDimension(if (large) R.dimen.lib_media_playerPlayFileNameWidthLarge else R.dimen.lib_media_playerPlayFileNameWidth)

        val playButtonMarginSidesLp = (viewImpl.ivPlay.layoutParams as LinearLayout.LayoutParams)
        val margin = viewImpl.context.resources.getDimension(if (large) R.dimen.lib_media_playerPlayButtonMarinSidesLarge else R.dimen.lib_media_playerPlayButtonMarinSides)
        playButtonMarginSidesLp.setMargins(margin.toInt(), 0, margin.toInt(), 0)

        toggleDirectionView(large)
    }

    private fun toggleDirectionView(zoomOut: Boolean) {
        if (zoomOut) {
            viewImpl.ivDirection.setImageResource(R.drawable.icon_video_player_zoom_in)
            viewImpl.ivDirection.setOnClickListener {
                iControlCallback?.portrait()
            }
        } else {
            viewImpl.ivDirection.setImageResource(R.drawable.icon_video_player_zoom_out)
            viewImpl.ivDirection.setOnClickListener {
                iControlCallback?.landscape()
            }
        }
    }

    private fun listeningSeekTacking(listen: Boolean) {
        if (listen) {
            handler.removeCallbacks(mProgressTicker)
            handler.post(mProgressTicker)
        } else {
            handler.removeCallbacks(mProgressTicker)
        }
    }

    private fun listeningControlVisible(listen: Boolean) {
        if (listen) {
            handler.removeCallbacks(mDismissIndicator)
            handler.postDelayed(mDismissIndicator, dismissInterval)
        } else {
            handler.removeCallbacks(mDismissIndicator)
        }
    }

    private var viewVisible = false

    fun toggleVisible() {
        toggleVisible(!this.viewVisible)
    }

    fun toggleVisible(
            visible: Boolean,
            autoDismiss: Boolean = true,
            animation: Boolean = true
    ) {
        if (viewVisible == visible) return
        viewVisible = visible
        if (visible) {
            listeningControlVisible(autoDismiss)
        } else {
            listeningControlVisible(false)
        }
        toggleVisibleAnimation(visible, arrayOf(viewImpl, viewImplTitle), animation)
    }

    private fun toggleVisibleAnimation(
            visible: Boolean,
            targetViews: Array<View>,
            animation: Boolean = true
    ) {
        if (animation) {
            for (view in targetViews) {
                view.animate().alpha(if (visible) 1f else 0f)
                        .setDuration(if (visible) visibleDuration else inVisibleDuration)
                        .withEndAction {
                            view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
                        }
            }
        } else {
            for (view in targetViews) {
                view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    fun destroy() {
        listeningSeekTacking(false)
        listeningControlVisible(false)
    }
}
