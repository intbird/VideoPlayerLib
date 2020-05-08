package intbird.soft.lib.video.player.main.controller.touch

import android.graphics.Rect
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.lib_media_player_touch.view.*
import intbird.soft.lib.video.player.R
import intbird.soft.lib.video.player.main.controller.touch.call.IVideoTouchCallback
import intbird.soft.lib.video.player.main.controller.touch.type.PlayerTouchType
import intbird.soft.lib.video.player.main.locker.call.ILockCallback
import intbird.soft.lib.video.player.main.notify.ILandscapeExecute
import intbird.soft.lib.video.player.main.notify.ILockExecute
import intbird.soft.lib.video.player.main.notify.mode.AdjustInfo
import intbird.soft.lib.video.player.main.notify.mode.ProgressInfo
import intbird.soft.lib.video.player.main.player.IPlayer
import intbird.soft.lib.video.player.utils.MediaTimeUtil
import kotlin.math.abs

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 * viewImpl: 暂时没时间view接口,直接用view实现
 */
class TouchController(private val player: IPlayer?, private val iLockCall: ILockCallback?,
                      private val videoTouchCallback: IVideoTouchCallback,
                      private var viewImpl: View) : ILockExecute, ILandscapeExecute {
    private var tapInterceptor = GestureDetector(videoTouchCallback.getContext(), PlayerTapInterceptor())
    private var touchInterceptor = PlayerTouchInterceptor()

    private val mediaTotalTime
        get() = player?.getTotalTime()?: 0L

    private val mediaCurrentTime
        get() = player?.getCurrentTime()?: 0L

    init {
        executeLock(false)
    }

    override fun executeLock(lock: Boolean) {
        if (lock) {
            viewImpl.setOnTouchListener { _, _ -> iLockCall?.needUnLock(); false }
        } else {
            viewImpl.setOnTouchListener { view, event -> touchInterceptor.onTouch(view, event) || tapInterceptor.onTouchEvent(event) }
        }
    }

    override fun onLandscape() {
        touchInterceptor.viewSizeChange()
    }

    override fun onPortrait() {
        touchInterceptor.viewSizeChange()
    }

    inner class PlayerTapInterceptor : SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            videoTouchCallback?.onSingleTap()
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            videoTouchCallback?.onDoubleTap()
            return true
        }
    }

    data class PlayerMoveBound(val lowBound: Int, var upBound: Int)

    inner class PlayerTouchInterceptor() : View.OnTouchListener {
        // 触摸记录
        private var lastTouchEventX: Float = 0f
        private var lastTouchEventY: Float = 0f
        private var lastTouchType: PlayerTouchType = PlayerTouchType.NONE

        // 进度视差因子
        private val parallaxX = 1f

        // 音量视差因子
        private val parallaxYVolume = 4.4f

        // 亮度视差因子
        private val parallaxYLight = 4.4f

        // 回调进度阈值
        private val ratioThreshold = 0.01f

        // 横向滑动控制范围
        private var allowXAlixRange: Rect? = null
        private var allowXAlixMoveBound: PlayerMoveBound? = PlayerMoveBound(20, 20)

        // 纵向滑动控制范围
        private var allowYAlixRangeLeft: Rect? = null
        private var allowYAlixRangeRight: Rect? = null
        private var allowYAlixMoveBound: PlayerMoveBound? = PlayerMoveBound(20, 20)

        // 进度缓存
        private var lastProgressInfo = ProgressInfo()

        // 音量缓存
        private var adjustVolumeInfo = AdjustInfo()

        // 亮度缓存
        private var adjustBrightnessInfo = AdjustInfo()

        fun viewSizeChange() {
            allowXAlixRange = null
            allowYAlixRangeLeft = null
            allowYAlixRangeRight = null
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            val viewWidth = v?.width ?: 0
            val viewHeight = v?.height ?: 0
            // 不应用滑动
            if (viewWidth == 0 || viewHeight == 0) {
                return false
            }
            when (event?.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchEventX = event.x
                    lastTouchEventY = event.y

                    handleTouchDown(viewWidth, viewHeight)
                }
                MotionEvent.ACTION_MOVE -> {
                    val distanceX = event.x - lastTouchEventX
                    val distanceY = event.y - lastTouchEventY

                    return handlerTouchMove(distanceX, distanceY, viewWidth, viewHeight, event)
                }
                MotionEvent.ACTION_UP -> {
                    releaseTouchHandler()
                }
                else -> {
                }
            }
            return false
        }

        private fun handlerTouchMove(distanceX: Float, distanceY: Float, viewWidth: Int, viewHeight: Int, event: MotionEvent): Boolean {
            return when (lastTouchType) {
                PlayerTouchType.NONE -> {
                    if (isTouchProgress(distanceX, distanceY, viewWidth, event)) {
                        lastTouchType = PlayerTouchType.TOUCH_PROGRESS
                        videoTouchCallback.onBeforeDropSeek()
                    }
                    if (isTouchVolume(distanceX, distanceY, viewHeight, event)) {
                        lastTouchType = PlayerTouchType.TOUCH_VOLUME
                    }
                    if (isTouchLight(distanceX, distanceY, viewHeight, event)) {
                        lastTouchType = PlayerTouchType.TOUCH_LIGHT
                    }
                    return lastTouchType != PlayerTouchType.NONE
                }
                PlayerTouchType.TOUCH_PROGRESS -> {
                    touchProgress(distanceX, distanceY, viewWidth, event)
                }
                PlayerTouchType.TOUCH_VOLUME -> {
                    touchVolume(distanceX, distanceY, viewHeight, event)
                }
                PlayerTouchType.TOUCH_LIGHT -> {
                    touchLight(distanceX, distanceY, viewHeight, event)
                }
            }
        }

        private fun handleTouchDown(viewWidth: Int, viewHeight: Int) {
            // 横向进度触摸范围
            if (null == allowXAlixRange) {
                allowXAlixRange = Rect(0, 0, viewWidth, viewHeight)
            }
            if (null == allowYAlixRangeLeft) {
                allowYAlixRangeLeft = Rect(0, viewHeight / 6 * 1, viewWidth / 2, viewHeight / 6 * 5)
            }
            if (null == allowYAlixRangeRight) {
                allowYAlixRangeRight = Rect(viewWidth / 2, viewHeight / 6 * 1, viewWidth, viewHeight / 6 * 5)
            }

            lastProgressInfo.available = false
            adjustVolumeInfo.available = false
            adjustBrightnessInfo.available = false
        }

        private fun isTouchProgress(distanceX: Float, distanceY: Float, viewWidth: Int, event: MotionEvent): Boolean {
            return allowXAlixRange!!.contains(event.x.toInt(), event.y.toInt())
                    && (abs(distanceY) < allowXAlixMoveBound!!.lowBound) && (abs(distanceX) > allowXAlixMoveBound!!.upBound)
        }

        private fun isTouchVolume(distanceX: Float, distanceY: Float, viewHeight: Int, event: MotionEvent): Boolean {
            return allowYAlixRangeRight!!.contains(event.x.toInt(), event.y.toInt())
                    && (abs(distanceX) < allowYAlixMoveBound!!.lowBound) && (abs(distanceY) > allowYAlixMoveBound!!.upBound)
        }

        private fun isTouchLight(distanceX: Float, distanceY: Float, viewHeight: Int, event: MotionEvent): Boolean {
            return allowYAlixRangeLeft!!.contains(event.x.toInt(), event.y.toInt())
                    && (abs(distanceX) < allowYAlixMoveBound!!.lowBound) && (abs(distanceY) > allowYAlixMoveBound!!.upBound)
        }

        private fun releaseTouchHandler() {
            when (lastTouchType) {
                PlayerTouchType.NONE -> {
                }
                PlayerTouchType.TOUCH_PROGRESS -> {
                    releaseProgressTouch()
                }
                PlayerTouchType.TOUCH_VOLUME -> {
                    releaseVolumeTouch()
                }
                PlayerTouchType.TOUCH_LIGHT -> {
                    releaseLightTouch()
                }
            }
            lastTouchType = PlayerTouchType.NONE
        }

        private fun touchProgress(distanceX: Float, distanceY: Float, viewWidth: Int, event: MotionEvent): Boolean {
            val radioX = distanceX / viewWidth   // 滑动长度占比
            // 阈值
            if (abs(radioX) > 0.01) {
                // 计算进度值
                if (!lastProgressInfo.available) {
                    lastProgressInfo = ProgressInfo(0L, mediaTotalTime, mediaCurrentTime)
                }
                lastProgressInfo.addIncrease(radioX * parallaxX)
                videoTouchCallback.onDroppingSeek(lastProgressInfo.progress)
                // 播放控制
                // videoTouchCallback?.notifyVideoProgressImpl(newVideoProgressTime, mediaTotalTime)
                visibleProgressIndicator(true)
                viewImpl.tvTouchCurrentProgress.text = MediaTimeUtil.formatTime(lastProgressInfo.progress)
                viewImpl.tvTouchTotalProgress.text = MediaTimeUtil.formatTime(mediaTotalTime)
                viewImpl.pbTouchProgress.progress = lastProgressInfo.progressUI
                viewImpl.pbTouchProgress.max = lastProgressInfo.maxValueUI
            }
            return true
        }

        private fun releaseProgressTouch() {
            visibleProgressIndicator(false)
            videoTouchCallback.onAfterDropSeek()
        }

        private fun touchVolume(distanceX: Float, distanceY: Float, viewHeight: Int, event: MotionEvent): Boolean {
            val ratioY = -distanceY / viewHeight   // 滑动高度占比
            //阈值
            if (abs(ratioY) > ratioThreshold) {
                if (!adjustVolumeInfo.available) {
                    adjustVolumeInfo = videoTouchCallback.getVolumeInfo()
                }
                adjustVolumeInfo.addIncrease(ratioY * parallaxYVolume)
                // 音量调节实现让外部去做
                videoTouchCallback.changeSystemVolumeImpl(adjustVolumeInfo.progress)
                visibleAdjustIndicator(true)
                // 调整UI
                if (adjustVolumeInfo.progress <= 0) viewImpl.adjustIcon.setImageResource(R.drawable.icon_video_player_audio_off)
                else viewImpl.adjustIcon.setImageResource(R.drawable.icon_video_player_audio_on)
                viewImpl.adjustProgressBar.progress = adjustVolumeInfo.progressUI
                viewImpl.adjustProgressBar.max = adjustVolumeInfo.maxValueUI
            }
            return true
        }

        private fun releaseVolumeTouch() {
            visibleAdjustIndicator(false)
        }

        private fun touchLight(distanceX: Float, distanceY: Float, viewHeight: Int, event: MotionEvent): Boolean {
            val ratioY = -distanceY / viewHeight   // 滑动高度占比
            //阈值
            if (abs(ratioY) > ratioThreshold) {
                if (!adjustBrightnessInfo.available) {
                    adjustBrightnessInfo = videoTouchCallback.getBrightnessInfo()
                }
                adjustBrightnessInfo.addIncrease(ratioY * parallaxYLight)
                // 亮度调节实现让外部去做
                videoTouchCallback.changeBrightnessImpl(adjustBrightnessInfo.progress)
                visibleAdjustIndicator(true)
                // 调整UI
                if (adjustBrightnessInfo.progress <= 0) viewImpl.adjustIcon.setImageResource(R.drawable.icon_video_player_light_off)
                else viewImpl.adjustIcon.setImageResource(R.drawable.icon_video_player_light_on)
                viewImpl.adjustProgressBar.progress = adjustBrightnessInfo.progressUI
                viewImpl.adjustProgressBar.max = adjustBrightnessInfo.maxValueUI
            }
            return true
        }

        private fun releaseLightTouch() {
            visibleAdjustIndicator(false)
        }

        private fun visibleProgressIndicator(visible: Boolean) {
            if (visible) {
                if (viewImpl.llTimeIndicatorWrapper.visibility == View.INVISIBLE) {
                    viewImpl.llTimeIndicatorWrapper.visibility = View.VISIBLE
                }
            } else {
                if (viewImpl.llTimeIndicatorWrapper.visibility == View.VISIBLE) {
                    viewImpl.llTimeIndicatorWrapper.visibility = View.INVISIBLE
                }
            }
        }

        private fun visibleAdjustIndicator(visible: Boolean) {
            if (visible) {
                if (viewImpl.llAdjustIndicatorWrapper.visibility == View.INVISIBLE) {
                    viewImpl.llAdjustIndicatorWrapper.visibility = View.VISIBLE
                }
            } else {
                if (viewImpl.llAdjustIndicatorWrapper.visibility == View.VISIBLE) {
                    viewImpl.llAdjustIndicatorWrapper.visibility = View.INVISIBLE
                }
            }
        }
    }

    fun destroy() {

    }
}
