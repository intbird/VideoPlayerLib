package net.intbird.soft.lib.video.player.main.controller.control


import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.SeekBar
import net.intbird.soft.lib.video.player.R
import net.intbird.soft.lib.video.player.api.const.ConstConfigs
import net.intbird.soft.lib.video.player.main.controller.control.call.IControlCallback
import net.intbird.soft.lib.video.player.main.view.dialog.type.SingleChooseType
import net.intbird.soft.lib.video.player.main.locker.call.ILockCallback
import net.intbird.soft.lib.video.player.main.notify.ILandscapeExecute
import net.intbird.soft.lib.video.player.main.notify.ILockExecute
import net.intbird.soft.lib.video.player.main.player.IPlayer
import net.intbird.soft.lib.video.player.main.player.player.call.IParamsChange
import net.intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import net.intbird.soft.lib.video.player.utils.MediaTimeUtil
import kotlinx.android.synthetic.main.lib_media_player_control_comp_bottom_2.view.*
import kotlinx.android.synthetic.main.lib_media_player_control_comp_center.view.*
import kotlinx.android.synthetic.main.lib_media_player_control_comp_progress.view.*
import kotlinx.android.synthetic.main.lib_media_player_control_comp_title.view.*
import kotlinx.android.synthetic.main.lib_media_player_control_style_2.view.*
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
) : ILockExecute, ILandscapeExecute, IParamsChange {
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
        viewImpl?.tvRates?.setOnClickListener { showSingleDialog(SingleChooseType.RATES, true) ;}
        viewImpl?.ivTimedText?.setOnClickListener { showSingleDialog(SingleChooseType.TEXT, true) ; }
        viewImpl?.tvClarity?.setOnClickListener { showSingleDialog(SingleChooseType.CLARITY, true) ; }
        viewImpl?.tvClarityPortrait?.setOnClickListener { showSingleDialog(SingleChooseType.CLARITY, true) ; }
        viewImpl?.seekBarProgress?.setOnSeekBarChangeListener(onSeekBarChangeListener)
        viewImpl?.ivSeekBackward?.setOnClickListener { iControlCallback?.backward(seekToInterval);}
        viewImpl?.ivSeekBackwardCenter?.setOnClickListener { iControlCallback?.backward(seekToInterval); }
        viewImpl?.ivSeekForward?.setOnClickListener { iControlCallback?.forward(seekToInterval); }
        viewImpl?.ivSeekForwardCenter?.setOnClickListener { iControlCallback?.forward(seekToInterval);}
        viewImpl?.ivLastCenter?.setOnClickListener { iControlCallback?.last(); }
        viewImpl?.ivLast?.setOnClickListener { iControlCallback?.last(); }
        viewImpl?.ivNextCenter?.setOnClickListener { iControlCallback?.next(); }
        viewImpl?.ivNext?.setOnClickListener { iControlCallback?.next(); }

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

    override fun onParamsChange(mediaFileInfo: MediaFileInfo?) {
        viewImpl?.tvRates?.text = ConstConfigs.getText(mediaFileInfo?.speedRate)
        viewImpl?.tvVideoName?.text = mediaFileInfo?.mediaName ?: ""
        viewImpl?.tvClarity?.text = mediaFileInfo?.clarity ?: ""
        viewImpl?.tvClarityPortrait?.text = mediaFileInfo?.clarity ?: ""

        resultSingleDialog(SingleChooseType.CLARITY, !TextUtils.isEmpty(mediaFileInfo?.clarity))
        resultSingleDialog(SingleChooseType.RATES, (mediaFileInfo?.speedRate?:0) != 0)
        resultSingleDialog(SingleChooseType.TEXT, ConstConfigs.isVisible(mediaFileInfo?.subtitle))
    }

    fun onPrepared(mediaFileInfo: MediaFileInfo?) {
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
        showSingleDialog(SingleChooseType.NONE, false)
        toggleVisible(true)
        togglePlay(false)
        toggleLock(false)

        listeningSeekTacking(false)
        listeningControlVisible(false)
    }

    private fun updateSeekProgressUI() {
        if (null == player) return
        viewImpl?.seekBarProgress?.progress = mediaCurrentTime.toInt()
        viewImpl?.seekBarProgress?.max = mediaTotalTime.toInt()

        viewImpl?.tvVideoCurTime?.text = MediaTimeUtil.formatTime(mediaCurrentTime)
        viewImpl?.tvVideoTotalTime?.text = MediaTimeUtil.formatTime(mediaTotalTime)
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

        if (landscape) {
            viewImpl?.rlBottomControlHidden?.visibility = View.VISIBLE
            viewImpl?.llCenterControl?.visibility = View.GONE
            viewImpl?.ivDirectionPortrait?.visibility = View.GONE
            viewImpl?.tvClarityPortrait?.visibility = View.GONE
            viewImpl?.tvVideoName?.visibility = View.VISIBLE
        } else {
            viewImpl?.rlBottomControlHidden?.visibility = View.GONE
            viewImpl?.llCenterControl?.visibility = View.VISIBLE
            viewImpl?.ivDirectionPortrait?.visibility = View.VISIBLE
            viewImpl?.tvClarityPortrait?.visibility = View.VISIBLE
            viewImpl?.tvVideoName?.visibility = View.INVISIBLE
        }
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

    private fun showSingleDialog(singleChooseType: SingleChooseType, show: Boolean) {
        toggleVisible(!show)
        iControlCallback?.showDialog(singleChooseType, show)
    }

    private fun resultSingleDialog(singleChooseType: SingleChooseType, show: Boolean?=false) {
        when (singleChooseType) {
            SingleChooseType.NONE -> {
            }
            SingleChooseType.TEXT -> {
                viewImpl?.ivTimedText?.visibility =
                    if (show == true) View.VISIBLE else View.INVISIBLE
            }
            SingleChooseType.RATES -> {
                viewImpl?.tvRates?.visibility =
                    if (show == true) View.VISIBLE else View.INVISIBLE
            }
            SingleChooseType.CLARITY -> {
                viewImpl?.tvClarity?.visibility =
                    if (show == true) View.VISIBLE else View.GONE
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
