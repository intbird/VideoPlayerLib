package intbird.soft.lib.video.player.main.controller.control


import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.lib_media_player_control_style_1.view.ivDirection
import kotlinx.android.synthetic.main.lib_media_player_control_style_1.view.ivPlay
import kotlinx.android.synthetic.main.lib_media_player_control_style_1.view.ivLocker
import kotlinx.android.synthetic.main.lib_media_player_control_style_1.view.ivLast
import kotlinx.android.synthetic.main.lib_media_player_control_style_1.view.ivNext
import kotlinx.android.synthetic.main.lib_media_player_control_style_1.view.layoutBottomPanel
import kotlinx.android.synthetic.main.lib_media_player_control_style_1.view.seekBarProgress
import kotlinx.android.synthetic.main.lib_media_player_control_style_1.view.tvVideoCurTime
import kotlinx.android.synthetic.main.lib_media_player_control_style_1.view.tvVideoTotalTime
import kotlinx.android.synthetic.main.lib_media_player_control_style_2.view.ivDirectionPortrait
import kotlinx.android.synthetic.main.lib_media_player_control_style_2.view.ivPlayCenter
import kotlinx.android.synthetic.main.lib_media_player_control_style_2.view.ivSeekBackward
import kotlinx.android.synthetic.main.lib_media_player_control_style_2.view.ivSeekBackwardCenter
import kotlinx.android.synthetic.main.lib_media_player_control_style_2.view.ivSeekForward
import kotlinx.android.synthetic.main.lib_media_player_control_style_2.view.ivSeekForwardCenter
import kotlinx.android.synthetic.main.lib_media_player_control_style_2.view.layoutCenterPanel
import kotlinx.android.synthetic.main.lib_media_player_control_style_2.view.llCenterControl
import kotlinx.android.synthetic.main.lib_media_player_control_style_2.view.rlBottomControl2
import kotlinx.android.synthetic.main.lib_media_player_control_style_2.view.tvClarity
import kotlinx.android.synthetic.main.lib_media_player_control_style_2.view.tvRates
import intbird.soft.lib.video.player.R
import intbird.soft.lib.video.player.main.controller.control.call.IControlCallback
import intbird.soft.lib.video.player.main.locker.call.ILockCallback
import intbird.soft.lib.video.player.main.notify.ILandscapeExecute
import intbird.soft.lib.video.player.main.notify.ILockExecute
import intbird.soft.lib.video.player.main.player.IPlayer
import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import intbird.soft.lib.video.player.utils.MediaTimeUtil
import java.util.concurrent.TimeUnit

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 *
 * viewImpl: 暂时不写view接口,直接用view实现
 */
open class ControlController(
    private val player: IPlayer?,
    private val lockCallback: ILockCallback?,
    private val iControlCallback: IControlCallback?,
    private val viewImpl: View?
) : ILockExecute, ILandscapeExecute {
    private var handler = Handler(Looper.getMainLooper())
    private val progressInterval = 1000L
    private val dismissInterval = 3000L

    private val seekToInterval = TimeUnit.SECONDS.toMillis(10)
    private val visibleDuration = 200L
    private val inVisibleDuration = 400L

    private val mediaTotalTime: Long
        get() = player?.getTotalTime() ?: 0L

    private val mediaCurrentTime: Long
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
                        viewImpl?.tvVideoCurTime?.text = MediaTimeUtil.formatTime(i.toLong())
                        iControlCallback?.onDroppingSeek(i.toLong())
                    }
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    iControlCallback?.onAfterDropSeek()
                }
            }

    private var controlControllerEnable = true

    init {
        viewImpl?.setOnTouchListener { _, _ -> if(controlControllerEnable) toggleVisible(); false }
        viewImpl?.tvClarity?.setOnClickListener { toggleCover(1, true) }
        viewImpl?.tvRates?.setOnClickListener { toggleCover(2, true) }
        viewImpl?.seekBarProgress?.setOnSeekBarChangeListener(onSeekBarChangeListener)
        viewImpl?.ivSeekBackward?.setOnClickListener { iControlCallback?.backward(seekToInterval) }
        viewImpl?.ivSeekBackwardCenter?.setOnClickListener { iControlCallback?.backward(seekToInterval) }
        viewImpl?.ivSeekForward?.setOnClickListener { iControlCallback?.forward(seekToInterval) }
        viewImpl?.ivSeekForwardCenter?.setOnClickListener { iControlCallback?.forward(seekToInterval) }
        viewImpl?.ivLast?.setOnClickListener { iControlCallback?.last() }
        viewImpl?.ivNext?.setOnClickListener { iControlCallback?.next() }

        toggleLock(false)
        togglePlay(false)
        toggleDirection(false)
    }

    override fun executeLock(lock: Boolean) {
        if (lock) {
            viewImpl?.setOnTouchListener { _, _ ->  lockCallback?.needLock(); false }
        } else {
            viewImpl?.setOnTouchListener { _, _ -> if(controlControllerEnable) toggleVisible(); false }
        }
        toggleLock(lock)
    }

    override fun onLandscape() {
        this.controlControllerEnable = false
        toggleDirection(true)
    }

    override fun onPortrait() {
        this.controlControllerEnable = true
        toggleDirection(false)
    }

    fun onPrepared(mediaFileInfo: MediaFileInfo) {
        viewImpl?.findViewById<TextView>(R.id.tvVideoName)?.text = mediaFileInfo.mediaName ?: ""
        viewImpl?.tvClarity?.text = mediaFileInfo.clarity ?: ""
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
        toggleCover(0, false)
        toggleVisible(true)
        togglePlay(false)
        toggleLock(false)

        listeningSeekTacking(false)
        listeningControlVisible(false)
    }

    private fun updateSeekProgressUI() {
        if (null != player) {
            viewImpl?.seekBarProgress?.progress = mediaCurrentTime.toInt()
            viewImpl?.seekBarProgress?.max = mediaTotalTime.toInt()

            viewImpl?.tvVideoCurTime?.text = MediaTimeUtil.formatTime(mediaCurrentTime)
            viewImpl?.tvVideoTotalTime?.text = MediaTimeUtil.formatTime(mediaTotalTime)
        }
    }

    private fun togglePlay(play: Boolean) {
        updateSeekProgressUI()
        if (play) {
            viewImpl?.ivPlay?.setImageResource(R.drawable.icon_video_player_pause)
            viewImpl?.ivPlayCenter?.setImageResource(R.drawable.icon_video_player_pause)

            val clicker = View.OnClickListener { togglePlay(false); player?.pause() }
            viewImpl?.ivPlay?.setOnClickListener(clicker)
            viewImpl?.ivPlayCenter?.setOnClickListener(clicker)
        } else {
            viewImpl?.ivPlay?.setImageResource(R.drawable.icon_video_player_play)
            viewImpl?.ivPlayCenter?.setImageResource(R.drawable.icon_video_player_play)

            val clicker = View.OnClickListener { togglePlay(true); player?.start() }
            viewImpl?.ivPlay?.setOnClickListener(clicker)
            viewImpl?.ivPlayCenter?.setOnClickListener(clicker)
        }
    }

    private fun toggleLock(lock: Boolean) {
        toggleVisible(!lock)
        if (lock) {
            viewImpl?.ivLocker?.setImageResource(R.drawable.icon_video_player_lock)
            viewImpl?.ivLocker?.setOnClickListener {
                lockCallback?.needUnLock(false)
            }
        } else {
            viewImpl?.ivLocker?.setImageResource(R.drawable.icon_video_player_unlock)
            viewImpl?.ivLocker?.setOnClickListener {
                lockCallback?.needLock()
            }
        }
    }

    private fun toggleDirection(landscape: Boolean) {
        toggleDirectionView(landscape)
        toggleLandscapeLayout(landscape)

        if (landscape) {
            viewImpl?.rlBottomControl2?.visibility = View.VISIBLE
            viewImpl?.llCenterControl?.visibility = View.GONE
            viewImpl?.ivDirectionPortrait?.visibility = View.GONE
            viewImpl?.findViewById<TextView>(R.id.tvVideoName)?.visibility = View.VISIBLE
        } else {
            viewImpl?.rlBottomControl2?.visibility = View.GONE
            viewImpl?.llCenterControl?.visibility = View.VISIBLE
            viewImpl?.ivDirectionPortrait?.visibility = View.VISIBLE
            viewImpl?.findViewById<TextView>(R.id.tvVideoName)?.visibility = View.GONE
        }
    }

    private fun toggleLandscapeLayout(landscape: Boolean) {
        val titleTextLp = viewImpl?.findViewById<TextView>(R.id.tvVideoName)?.layoutParams as? LinearLayout.LayoutParams
        titleTextLp?.weight =
            getDimens(if (landscape) R.dimen.lib_media_playerPlayFileNameWidthLarge else R.dimen.lib_media_playerPlayFileNameWidth)

        val playButtonMarginSidesLp = (viewImpl?.ivPlay?.layoutParams as? LinearLayout.LayoutParams)
        val playButtonSideMargin =
            getDimens(if (landscape) R.dimen.lib_media_playerPlayButtonMarinSidesLarge else R.dimen.lib_media_playerPlayButtonMarinSides)
        playButtonMarginSidesLp?.setMargins(
            playButtonSideMargin.toInt(),
            0,
            playButtonSideMargin.toInt(),
            0
        )

        val clarityRightMarginLp = viewImpl?.tvClarity?.layoutParams as? RelativeLayout.LayoutParams
        val clarityRightMargin =
            getDimens(if (landscape) R.dimen.lib_media_playerClarityRightMarginLarge else R.dimen.lib_media_playerPlayButtonMarinSides)
        clarityRightMarginLp?.setMargins(0, 0, clarityRightMargin.toInt(), 0)
    }

    private fun getDimens(rid: Int): Float {
        return viewImpl?.context?.resources?.getDimension(rid) ?: 0f
    }

    private fun toggleDirectionView(zoomOut: Boolean) {
        if (zoomOut) {
            viewImpl?.ivDirectionPortrait?.setImageResource(R.drawable.icon_video_player_zoom_in)
            viewImpl?.ivDirection?.setImageResource(R.drawable.icon_video_player_zoom_in)

            val clicker = View.OnClickListener { iControlCallback?.portrait() }
            viewImpl?.ivDirectionPortrait?.setOnClickListener(clicker)
            viewImpl?.ivDirection?.setOnClickListener(clicker)
        } else {
            viewImpl?.ivDirectionPortrait?.setImageResource(R.drawable.icon_video_player_zoom_out)
            viewImpl?.ivDirection?.setImageResource(R.drawable.icon_video_player_zoom_out)

            val clicker = View.OnClickListener { iControlCallback?.landscape() }
            viewImpl?.ivDirectionPortrait?.setOnClickListener(clicker)
            viewImpl?.ivDirection?.setOnClickListener(clicker)
        }
    }

    private fun toggleCover(coverType: Int, showCover: Boolean) {
        toggleVisible(!showCover)

        if (coverType == 0 || coverType == 1) {
            iControlCallback?.showClarity(showCover)
        }
        if (coverType == 0 || coverType == 2) {
            iControlCallback?.showRates(showCover)
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
        toggleVisibleAnimation(
            visible,
            arrayOf(viewImpl?.findViewById(R.id.llTopTitle), viewImpl?.layoutCenterPanel, viewImpl?.layoutBottomPanel),
            animation
        )
    }

    private fun toggleVisibleAnimation(
        visible: Boolean,
        targetViews: Array<View?>,
        animation: Boolean = true
    ) {
        if (animation) {
            for (view in targetViews) {
                view?.animate()?.alpha(if (visible) 1f else 0f)
                    ?.setDuration(if (visible) visibleDuration else inVisibleDuration)
                    ?.withEndAction {
                        view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
                    }
            }
        } else {
            for (view in targetViews) {
                view?.visibility = if (visible) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    fun destroy() {
        listeningSeekTacking(false)
        listeningControlVisible(false)
    }
}
