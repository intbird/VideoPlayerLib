package intbird.soft.lib.video.player.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import intbird.soft.lib.video.player.R
import intbird.soft.lib.video.player.api.bean.MediaCheckedData
import intbird.soft.lib.video.player.api.bean.MediaPlayItem
import intbird.soft.lib.video.player.api.bean.MediaPlayItemInfo
import intbird.soft.lib.video.player.api.state.IVideoPlayerCallback
import intbird.soft.lib.video.player.api.state.IVideoPlayerController
import intbird.soft.lib.video.player.api.state.IVideoPlayerStateInfo
import intbird.soft.lib.video.player.main.controller.control.ControlController
import intbird.soft.lib.video.player.main.controller.control.call.IControlCallback
import intbird.soft.lib.video.player.main.controller.touch.TouchController
import intbird.soft.lib.video.player.main.controller.touch.call.IVideoTouchCallback
import intbird.soft.lib.video.player.main.dialog.SingleChooseCallback
import intbird.soft.lib.video.player.main.dialog.SingleChooseDialogFragment
import intbird.soft.lib.video.player.main.dialog.type.SingleChooseType
import intbird.soft.lib.video.player.main.locker.LockController
import intbird.soft.lib.video.player.main.notify.ILockExecute
import intbird.soft.lib.video.player.main.notify.ITouchSystemExecute
import intbird.soft.lib.video.player.main.notify.mode.AdjustInfo
import intbird.soft.lib.video.player.main.player.IPlayer
import intbird.soft.lib.video.player.main.player.call.IPlayerCallback
import intbird.soft.lib.video.player.main.player.call.IPlayerExecute
import intbird.soft.lib.video.player.main.player.call.PlayerCallbacks
import intbird.soft.lib.video.player.main.player.intent.MediaIntentHelper
import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import intbird.soft.lib.video.player.main.player.player.ExoPlayerImpl
import intbird.soft.lib.video.player.main.player.player.MediaPlayerImpl
import intbird.soft.lib.video.player.main.view.MediaPlayerType
import intbird.soft.lib.video.player.main.view.MediaViewInfo
import intbird.soft.lib.video.player.main.view.MediaViewProvider
import intbird.soft.lib.video.player.utils.MediaLightUtils
import intbird.soft.lib.video.player.utils.MediaLogUtil
import intbird.soft.lib.video.player.utils.MediaScreenUtils
import kotlinx.android.synthetic.main.lib_media_player_control_pop.*
import kotlinx.android.synthetic.main.lib_media_player_control_title.*
import kotlinx.android.synthetic.main.lib_media_player_main.*
import kotlinx.android.synthetic.main.lib_media_player_touch.*
import kotlin.properties.Delegates

/**
 * created by intbird
 * on 2020/9/1
 * DingTalk id: intbird
 *
 * 时间有限,有空就写
 */

open class VideoPlayerFragment : Fragment(), ILockExecute, IPlayerExecute {

    companion object {
        var EXTRA_PLAYER_TYPE = "videoType"

        var EXTRA_FILE_URLS = "videoUrls"
        var EXTRA_FILE_INDEX = "videoIndex"
        var EXTRA_PLAYER_AUTO_PLAY = "videoAutoPlay"

        fun newInstance(playList: ArrayList<MediaPlayItem>?, playIndex: Int, playerType: MediaPlayerType, autoPlay:Boolean): VideoPlayerFragment {
            val fragment = VideoPlayerFragment()
            var args = fragment.arguments
            if (null == args) args = Bundle()
            args.putParcelableArrayList(EXTRA_FILE_URLS, playList)
            args.putInt(EXTRA_FILE_INDEX, playIndex)
            args.putSerializable(EXTRA_PLAYER_TYPE, playerType)
            args.putBoolean(EXTRA_PLAYER_AUTO_PLAY, autoPlay)
            fragment.arguments = args
            return fragment
        }
    }

    private val permissionRequestCode = 10
    private val permissionSettingsRequestCode = 11
    private var sdcardPermissionsGrand by Delegates.observable(false) { _, _, newValue ->
        if (newValue) {
            intentHelper?.delegatePlay()
        }
        log("sdcardPermissionsGrand: $newValue")
    }

    private var intentHelper: MediaIntentHelper? = null
    private var playingMediaInfo = MediaFileInfo()

    private var player: IPlayer? = null
    private var playerView: MediaViewInfo<out View, out View>? = null
    private var playerStates:PlayerCallbacks? = null

    private var locker: LockController? = null
    private var videoTouchController: TouchController? = null
    private var videoControlController: ControlController? = null

    class SharedViewModel : ViewModel() {
        val landscape = MutableLiveData<Boolean>()
    }
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.lib_media_player_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intentHelper = MediaIntentHelper(arguments, intentCallback)
        instanceMediaPlayer(intentHelper?.mediaPlayerType)

        locker = LockController(ivPopLock)
        videoTouchController = TouchController(player, locker, touchCallback, layoutTouchPanel)
        videoControlController = ControlController(player, locker, controlCallback, playerView?.control)

        locker?.addExecute(videoTouchController)
            ?.addExecute(videoControlController)
            ?.addExecute(this)
        executeLock(false)

        handBackPressed()
    }

    private fun instanceMediaPlayer(mediaPlayerType: MediaPlayerType?) {
        when(mediaPlayerType) {
            MediaPlayerType.PLAYER_STYLE_1, MediaPlayerType.PLAYER_STYLE_2 -> {
                playerStates = PlayerCallbacks(playerCallback, videoPlayerCallback)
                playerView = MediaViewProvider(view).by(mediaPlayerType)
                player = MediaPlayerImpl(getInternalActivity(), playerView?.display as? TextureView, intentHelper, playerStates)
            }
            MediaPlayerType.PLAYER_STYLE_3 -> {
                playerStates = PlayerCallbacks(playerCallback, videoPlayerCallback)
                playerView = MediaViewProvider(view).by(mediaPlayerType)
                player = ExoPlayerImpl(getInternalActivity(), playerView?.display as? TextureView, playerStates)
            }
        }
    }

    private fun handBackPressed() {
        // when onBackPressed reset screen orientation
        getInternalActivity().onBackPressedDispatcher.addCallback(object :
            OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                log("handleOnBackPressed")
            }
        })

        ivBack?.setOnClickListener {
            log("ivBack ${resources.configuration.orientation}  ${getInternalActivity().requestedOrientation}")
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getInternalActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                getInternalActivity().finish()
            }
        }
    }

    private fun getInternalActivity(): FragmentActivity {
        return requireActivity()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkPermissionManifest(getInternalActivity(), permissionRequestCode)
        checkPermissionSettings(getInternalActivity(), permissionSettingsRequestCode)
        mediaStateCallback?.onCreated(this)
    }

    override fun onResume() {
        super.onResume()
        player?.resume()
        log("onResume")
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
        log("onPause")
    }

    protected fun isFinishing(): Boolean {
        return (isRemoving || isDetached || !isAdded)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.destroy()
        locker?.destroy()
        intentHelper?.onDestroy()
        videoTouchController?.destroy()
        videoControlController?.destroy()
        log("onDestroy")
    }

    protected fun keepViewScreen(screenOn: Boolean) {
        mediaRootContainer.keepScreenOn = screenOn
    }

    override fun executeLock(lock: Boolean) {
        // 方向锁定
        lockScreenOrientation(getInternalActivity(), lock)
        // 是否禁用自动转屏
        if (MediaLightUtils.checkSystemWritePermission(getInternalActivity()))
            Settings.System.putInt(
                getInternalActivity().contentResolver,
                Settings.System.ACCELEROMETER_ROTATION,
                if (lock) 0 else 1
            )
    }

    private fun lockScreenOrientation(activity: Activity, lock: Boolean) {
        val display = activity.windowManager.defaultDisplay
        when (display.rotation) {
            // 横屏
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if(lock) {
                    this.getInternalActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
                } else {
                    this.getInternalActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                }
            }
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if(lock) {
                    this.getInternalActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
                } else {
                    this.getInternalActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                }
            }
        }
    }

    private fun checkPermissionSettings(activity: Activity, requestCode: Int) {
        val permission = MediaLightUtils.checkSystemWritePermission(activity)
        if (!permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + activity.packageName)
                activity.startActivityForResult(intent, requestCode)
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_SETTINGS),
                    requestCode
                )
            }
        }
    }

    private fun checkPermissionManifest(context: Activity, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            sdcardPermissionsGrand = true
        } else {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                requestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty()) {
                for (index in grantResults.indices) {
                    if ((grantResults[index] == PackageManager.PERMISSION_GRANTED) && (permissions[index] == Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        sdcardPermissionsGrand = true
                    }
                }
            }
        } else if (requestCode == permissionSettingsRequestCode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var hasPermission = Settings.System.canWrite(getInternalActivity())
            } else if (grantResults.isNotEmpty()) {
                for (index in grantResults.indices) {
                    if (permissions[index] == Manifest.permission.WRITE_SETTINGS) {
                    }
                }
            }
        }
    }

    //----传入数据解析start----
    private val intentCallback = object : MediaIntentHelper.MediaIntentHelperCall {
        override fun getVideoCurrentTime(): Long? {
            return player?.getCurrentTime()
        }

        override fun onReceivePlayFile(autoPlay: Boolean, mediaFileInfo: MediaFileInfo?) {
            if (null == mediaFileInfo) return
            if (autoPlay) player?.prepare(mediaFileInfo)

            player?.onParamsChange(mediaFileInfo)
            videoControlController?.onParamsChange(mediaFileInfo)
            log("onReceivePlayFile: $mediaFileInfo")
        }
    }
    //----end----

    //----显示器,播放器,触摸和控制功能回调 start----
    private val playerCallback = object : IPlayerCallback {

        override fun onPrepare(mediaFileInfo: MediaFileInfo?) {
            contentLoading.show()
        }

        override fun onPrepared(mediaFileInfo: MediaFileInfo?) {
            contentLoading.hide()
            videoControlController?.onPrepared(mediaFileInfo)
        }

        override fun onReady(mediaFileInfo: MediaFileInfo, ready: Boolean) {
            log("onReady : $ready")
            if (!ready) return
            // auto play
            if (ready && (intentHelper?.isAutoStartPlay == true)) {
                player?.start()
            }
            // continue play
            val lastProgress = intentHelper?.getLastPlayingProgress(mediaFileInfo.mediaId) ?: 0
            if (ready && (lastProgress > 0)) {
                player?.seekTo(lastProgress, player?.isPlaying() == true)
                log("go on play: $lastProgress")
            }
        }

        override fun onStart() {
            contentLoading.hide()
            videoControlController?.onStart()
            keepViewScreen(true)
            log("player onStart")
        }

        override fun onSeekTo(duration: Long) {
            videoControlController?.onSeekTo(duration)
            log("player onSeekTo$duration")
        }

        override fun onPause() {
            videoControlController?.onPause()
            log("player onPause")
        }

        override fun onCompletion(mediaFileInfo: MediaFileInfo?) {
            videoControlController?.onCompletion()
            keepViewScreen(false)
            log("player onCompletion:$mediaFileInfo")
        }

        override fun onStop() {
            contentLoading.hide()
            videoControlController?.onStop()
            log("player onStop")
        }

        override fun onError(errorCode:Int, errorMessage: String?) {
            contentLoading.hide()
            videoControlController?.onStop()
            log("player onError")
        }

        override fun onVideoSizeChanged(mediaFileInfo: MediaFileInfo?) {
            if(null != mediaFileInfo) playingMediaInfo = mediaFileInfo
            log("player onVideoSizeChanged:$playingMediaInfo")
            setFitToFillAspectRatio(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        }

        override fun onBuffStart() {
            contentLoading.show()
        }

        override fun onBuffEnded() {
            contentLoading.hide()
        }
    }

    private val touchCallback = object : IVideoTouchCallback {
        override fun onSingleTap() {
            videoControlController?.toggleVisible()
        }

        override fun onDoubleTap() {
            if (player?.isPlaying() == true) player?.pause() else player?.start()
            videoControlController?.toggleVisible(player?.isPlaying() == false)
        }

        override fun onBeforeDropSeek() {
            this@VideoPlayerFragment.onBeforeDropSeek(false)
        }

        override fun onDroppingSeek(progress: Long) {
            this@VideoPlayerFragment.onDroppingSeek(progress)
        }

        override fun onAfterDropSeek() {
            this@VideoPlayerFragment.onAfterDropSeek()
        }

        override fun getVolumeInfo(): AdjustInfo {
            return touchImpl.getVolumeInfo()
        }

        override fun changeSystemVolumeImpl(newVolume: Float) {
            touchImpl.changeSystemVolumeImpl(newVolume)
        }

        override fun getBrightnessInfo(): AdjustInfo {
            return touchImpl.getBrightnessInfo()
        }

        override fun changeBrightnessImpl(newBrightness: Float) {
            touchImpl.changeBrightnessImpl(newBrightness)
        }
    }

    private val touchImpl = object : ITouchSystemExecute {

        override fun getVolumeInfo(): AdjustInfo {
            val audioManager =
                getInternalActivity().getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            val min = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                audioManager?.getStreamMinVolume(AudioManager.STREAM_MUSIC)?.toFloat() ?: 0f else 0f
            val max = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)?.toFloat() ?: 0f
            val current = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)?.toFloat() ?: 0f
            return AdjustInfo(min, max, current)
        }

        override fun changeSystemVolumeImpl(newVolume: Float) {
            val audioManager =
                getInternalActivity().getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                newVolume.toInt(),
                AudioManager.FLAG_VIBRATE
            )
        }

        override fun getBrightnessInfo(): AdjustInfo {
            val current = (MediaLightUtils.getActivityBrightness(getInternalActivity()))
            return AdjustInfo(
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF,
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL,
                current
            )
        }

        override fun changeBrightnessImpl(newBrightness: Float) {
            MediaLightUtils.setAppBrightness(newBrightness, getInternalActivity())
        }
    }

    private val controlCallback = object : IControlCallback {

        override fun showDialog(dialogType: SingleChooseType, show: Boolean) {
            if (show) {
                SingleChooseDialogFragment.showDialog(parentFragmentManager,
                    intentHelper?.delegateCreateSingleChooseData(dialogType),
                    object : SingleChooseCallback {
                        override fun onChooseItem(index: Int, mediaCheckedData: MediaCheckedData) {
                            intentHelper?.delegateSelectedSingleChooseData(dialogType, index, mediaCheckedData)
                        }
                    }, view?.height)
            } else {
                SingleChooseDialogFragment.dismissDialog(parentFragmentManager)
            }
            log("rates: $show")
        }

        override fun backward(long: Long) {
            val progress = (player?.getCurrentTime() ?: 0) - long
            player?.seekTo(progress, player?.isPlaying()?:false)
            log("backward $progress")
        }

        override fun forward(long: Long) {
            val progress = (player?.getCurrentTime() ?: 0) + long
            player?.seekTo(progress, player?.isPlaying()?:false)
            log("forward $progress")
        }

        override fun last(): Boolean {
            return player?.last() == true
        }

        override fun next(): Boolean {
            return player?.next() == true
        }

        override fun landscape() {
            val currentOrientation = resources.configuration.orientation
            val activity: Activity = getInternalActivity()
            if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }

            log("landscape")
        }

        override fun portrait() {
            val currentOrientation = resources.configuration.orientation
            val activity: Activity = getInternalActivity()
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }

            log("portrait")
        }

        override fun onBeforeDropSeek() {
            this@VideoPlayerFragment.onBeforeDropSeek(true)
        }

        override fun onDroppingSeek(progress: Long) {
            this@VideoPlayerFragment.onDroppingSeek(progress)
        }

        override fun onAfterDropSeek() {
            this@VideoPlayerFragment.onAfterDropSeek()
        }
    }
    //----end----

    //---- 横竖屏处理 start---
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        log("onConfigurationChanged: orientation: ${newConfig.orientation}")
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewModel.landscape.value = true
            setFitToFillAspectRatio(true)
            videoControlController?.onLandscape()
            videoTouchController?.onLandscape()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewModel.landscape.value = false
            setFitToFillAspectRatio(false)
            videoControlController?.onPortrait()
            videoTouchController?.onPortrait()
        }
    }

    private val rootViewWidth by lazy {
        view?.width ?: MediaScreenUtils.getScreenWidth(
            getInternalActivity()
        )
    }
    private val rootViewHeight by lazy {
        view?.height ?: MediaScreenUtils.getScreenHeight(
            getInternalActivity()
        )
    }

    private fun setFitToFillAspectRatio(landscape: Boolean) {
        setFitToFillAspectRatio(
            landscape,
            rootViewWidth,
            rootViewHeight,
            playingMediaInfo.width,
            playingMediaInfo.height
        )
    }

    private fun setFitToFillAspectRatio(
        landscape: Boolean,
        viewWidth: Int,
        viewHeight: Int,
        videoWidth: Int,
        videoHeight: Int
    ) {
        val lp = displayPlaceholder.layoutParams
        val rate = videoWidth.toDouble() / videoHeight.toDouble()
        if (landscape) {
            lp.height = viewWidth
            lp.width = (viewWidth.toFloat() * rate).toInt()
        } else {
            lp.width = viewWidth
            lp.height = (viewWidth.toFloat() / rate).toInt()
        }
        displayPlaceholder.layoutParams = lp
        log("viewWidth: $viewWidth viewHeight:$viewHeight videoWidth:$videoWidth videoHeight:$videoHeight  lpW:${lp.width} lpH:${lp.height}")
    }

    //---- end ---

    //---- 进度条拖动 start ----
    private var targetProgress = 0L
    private var playingState = false

    fun onBeforeDropSeek(visible: Boolean) {
        targetProgress = 0L
        playingState = player?.isPlaying() ?: false
        if (playingState) {
            player?.pause()
        }
        videoControlController?.toggleVisible(
            visible,
            autoDismiss = false,
            animation = true
        )
    }

    fun onDroppingSeek(progress: Long) {
        //player?.seekTo(progress)
        targetProgress = progress
    }

    fun onAfterDropSeek() {
        player?.seekTo(targetProgress, playingState)
        videoControlController?.toggleVisible(true)
    }
    //---- end ----


    //---- 外部控制命令 start ----
    override fun registerPlayStateCallback(playerCallback: IVideoPlayerCallback) {
        mediaStateCallback = playerCallback
    }

    override fun setVideoPlayerList(playList: ArrayList<MediaPlayItem>?, playIndex: Int, autoPlay: Boolean) {
        if (isFinishing()) return
        intentHelper?.setVideoPlayerList(playList, playIndex, autoPlay)
    }

    override fun setVideoPlayerItem(mediaPlayItem: MediaPlayItem?, autoPlay: Boolean) {
        if (isFinishing()) return
        intentHelper?.setVideoPlayerItem(mediaPlayItem, autoPlay)
    }

    override fun setVideoPlayerItemInfo(mediaPlayItemInfo: MediaPlayItemInfo?, autoPlay: Boolean) {
        if (isFinishing()) return
        intentHelper?.setVideoPlayerItemInfo(mediaPlayItemInfo, autoPlay)
    }

    fun getVideoPlayerController(): IVideoPlayerController? {
        if (isFinishing()) return null
        return videoPlayerController
    }

    fun getVideoPlayerStateInfo(): IVideoPlayerStateInfo? {
        if (isFinishing()) return null
        return videoPlayerStateInfo
    }

    private var mediaStateCallback: IVideoPlayerCallback? = null

    fun setPlayerStateCallback(playerCallback: IVideoPlayerCallback) {
        this.mediaStateCallback = playerCallback
    }

    private val videoPlayerCallback = object : IPlayerCallback {
        override fun onPrepare(mediaFileInfo: MediaFileInfo?) {
            mediaStateCallback?.onPrepare()
        }

        override fun onPrepared(mediaFileInfo: MediaFileInfo?) {
            mediaStateCallback?.onPrepared()
        }

        override fun onReady(mediaFileInfo: MediaFileInfo, ready: Boolean) {
            //mediaStateCallback?.onReady()
        }

        override fun onStart() {
            mediaStateCallback?.onStart()
        }

        override fun onSeekTo(duration: Long) {
            mediaStateCallback?.onSeekTo(duration)
        }

        override fun onPause() {
            mediaStateCallback?.onPause(player?.getCurrentTime())
        }

        override fun onCompletion(mediaFileInfo: MediaFileInfo?) {
            mediaStateCallback?.onCompletion()
        }

        override fun onStop() {
            mediaStateCallback?.onStop()
        }

        override fun onError(errorCode: Int, errorMessage: String?) {
            mediaStateCallback?.onError(errorCode, errorMessage)
        }

        override fun onVideoSizeChanged(mediaFileInfo: MediaFileInfo?) {
        }

        override fun onBuffStart() {
            mediaStateCallback?.onBuffStart()
        }

        override fun onBuffEnded() {
            mediaStateCallback?.onBuffEnded()
        }
    }

    private val videoPlayerController = object : IVideoPlayerController {
        override fun start() {
            if (isFinishing()) return
            player?.start()
        }

        override fun seekTo(duration: Long, autoPlay: Boolean) {
            if (isFinishing()) return
            player?.seekTo(duration, autoPlay)
        }

        override fun pause() {
            if (isFinishing()) return
            player?.pause()
        }

        override fun stop() {
            if (isFinishing()) return
            player?.stop()
        }

        override fun last() {
            if (isFinishing()) return
            player?.last()
        }

        override fun next() {
            if (isFinishing()) return
            player?.next()
        }
    }

    private val videoPlayerStateInfo = object : IVideoPlayerStateInfo {

        override fun getVideoPlayingItem(): MediaPlayItem? {
            if (isFinishing()) return null
            return intentHelper?.playingItem
        }

        override fun getVideoPlayingItemInfo(): MediaPlayItemInfo? {
            if (isFinishing()) return null
            return intentHelper?.playingItemInfo
        }

        override fun getCurrentTime(): Long? {
            if (isFinishing()) return 0L
            return player?.getCurrentTime()
        }

        override fun getTotalTime(): Long? {
            if (isFinishing()) return 0L
            return player?.getTotalTime()
        }

        override fun isLocked(): Boolean? {
            if (isFinishing()) return false
            return locker?.isLocked()
        }
    }
    //---- end ----

    protected fun log(message: String) {
        MediaLogUtil.log("VideoFragment  $message")
    }
}