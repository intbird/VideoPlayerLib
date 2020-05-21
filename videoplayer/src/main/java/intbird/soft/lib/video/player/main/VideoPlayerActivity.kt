package intbird.soft.lib.video.player.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Surface
import android.view.TextureView
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.lib_media_player_control.*
import kotlinx.android.synthetic.main.lib_media_player_main.*
import kotlinx.android.synthetic.main.lib_media_player_touch.*
import intbird.soft.lib.video.player.R
import intbird.soft.lib.video.player.main.controller.control.ControlController
import intbird.soft.lib.video.player.main.controller.control.call.IControlCallback
import intbird.soft.lib.video.player.main.controller.touch.TouchController
import intbird.soft.lib.video.player.main.controller.touch.call.IVideoTouchCallback
import intbird.soft.lib.video.player.main.locker.LockController
import intbird.soft.lib.video.player.main.notify.ILockExecute
import intbird.soft.lib.video.player.main.notify.ITouchSystemExecute
import intbird.soft.lib.video.player.main.notify.mode.AdjustInfo
import intbird.soft.lib.video.player.main.player.IPlayer
import intbird.soft.lib.video.player.main.player.PlayerImpl
import intbird.soft.lib.video.player.main.player.call.IPlayerCallback
import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import intbird.soft.lib.video.player.utils.MediaLightUtils
import intbird.soft.lib.video.player.utils.MediaScreenUtils
import intbird.soft.lib.video.player.utils.MeidiaFileUtils
import kotlin.properties.Delegates

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 *
 * 时间有限
 */
class VideoPlayerActivity : Activity(), ILockExecute {

    companion object {
        var EXTRA_FILE_URLS = "videoUrls"
        var EXTRA_FILE_INDEX = "videoIndex"
    }

    private val permissionRequestCode = 100010
    private val permissionSettingsRequestCode = 100011
    private var sdcardPermissionsGrand by Delegates.observable(false) { _, oldValue, newValue ->
        if (newValue) {
            play(0, "error")
        }
    }

    private var videoUrls: Array<String>? = null
    private var videoIndex: Int = 0
    private var playingInfo = MediaFileInfo()

    private var player: IPlayer? = null
    private var locker: LockController? = null
    private var videoTouchController: TouchController? = null
    private var videoControlController: ControlController? = null

    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.lib_media_player_main)
        ivBack.setOnClickListener { finish() }
        textureView.surfaceTextureListener = surfaceChangeCallback

        locker = LockController(ivPopLock)
        player = PlayerImpl(playerCallback)
        videoTouchController = TouchController(player, locker, touchCallback, layoutTouchPanel)
        videoControlController =
            ControlController(player, locker, controlCallback, layoutControlPanel, llTopTitle)

        videoUrls = intent.getStringArrayExtra(EXTRA_FILE_URLS)
        videoIndex = intent.getIntExtra(EXTRA_FILE_INDEX, 0)

        checkPermissionManifest(this, permissionRequestCode)
        checkPermissionSettings(this, permissionSettingsRequestCode)

        locker?.addExecute(videoTouchController)
                ?.addExecute(videoControlController)
                ?.addExecute(this)

        executeLock(false)
    }

    override fun onResume() {
        super.onResume()
        player?.resume()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.destroy()
        locker?.destroy()
        videoTouchController?.destroy()
        videoControlController?.destroy()
    }

    private fun calScreenOrientation(activity: Activity): Int {
        val display = activity.windowManager.defaultDisplay
        return when (display.rotation) {
            // 横屏
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
            else -> {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
        }
    }

    override fun executeLock(lock: Boolean) {
        val orientation:Int = calScreenOrientation(this)
        // 方向锁定
        if (lock) {
            if (this.requestedOrientation != orientation) {
                this.requestedOrientation = orientation
            }
        } else {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        // 是否禁用自动转屏
        if (MediaLightUtils.checkSystemWritePermission(this))
            Settings.System.putInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, if (lock) 0 else 1)
    }

    private fun play(crease: Int, errorMessage: String) {
        val creasedFile = getCreasedFile(crease)
        if (null != creasedFile) {
            contentLoading.show()
            videoIndex = videoIndex.plus(crease)
            player?.prepare(creasedFile)
        } else {
            toast?.cancel()
            toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT)
            toast?.show()
        }
    }

    private fun getCreasedFile(crease: Int): MediaFileInfo? {
        if (null == videoUrls) {
            return null
        }
        val newIndex = videoIndex + crease
        if (newIndex < 0) {
            return null
        }
        if (newIndex > videoUrls!!.size - 1) {
            return null
        }
        val urlPath = videoUrls!![newIndex]
        if (urlPath.isEmpty()) {
            return null
        }
        val fileName = MeidiaFileUtils.getFileName(urlPath)
        if (fileName.isNullOrEmpty()) {
            return null
        }
        return MediaFileInfo(
            urlPath,
            fileName
        )
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
                var hasPermission = Settings.System.canWrite(this)
            } else if (grantResults.isNotEmpty()) {
                for (index in grantResults.indices) {
                    if (permissions[index] == Manifest.permission.WRITE_SETTINGS) {
                    }
                }
            }
        }
    }

    //----显示器,播放器,触摸和控制功能回调 start----
    private val surfaceChangeCallback = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture?,
            width: Int,
            height: Int
        ) {
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            return true
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            player?.available(Surface(surface))
        }
    }

    private val playerCallback = object : IPlayerCallback {

        override fun onPrepared(mediaFileInfo: MediaFileInfo) {
            contentLoading.hide()
            tvVideoName.text = mediaFileInfo.fileName ?: ""
            videoControlController?.onPrepared(mediaFileInfo)
        }

        override fun onStart() {
            contentLoading.hide()
            videoControlController?.onStart()
        }

        override fun onSeekTo(duration: Long) {
            videoControlController?.onSeekTo(duration)
        }

        override fun onPause() {
            videoControlController?.onPause()
        }

        override fun onCompletion(mediaFileInfo: MediaFileInfo) {
            contentLoading.hide()
            videoControlController?.onCompletion()
        }

        override fun onStop() {
            contentLoading.hide()
            videoControlController?.onStop()
        }

        override fun onVideoSizeChanged(mediaFileInfo: MediaFileInfo) {
            playingInfo = mediaFileInfo
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                onLandscape()
            } else if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                onPortrait()
            }
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
            this@VideoPlayerActivity.onBeforeDropSeek(false)
        }

        override fun onDroppingSeek(progress: Long) {
            this@VideoPlayerActivity.onDroppingSeek(progress)
        }

        override fun onAfterDropSeek() {
            this@VideoPlayerActivity.onAfterDropSeek()
        }

        override fun getContext(): Context {
            return touchImpl.getContext()
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
        override fun getContext(): Context {
            return this@VideoPlayerActivity
        }

        override fun getVolumeInfo(): AdjustInfo {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            val min = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                audioManager?.getStreamMinVolume(AudioManager.STREAM_MUSIC)?.toFloat() ?: 0f else 0f
            val max = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)?.toFloat() ?: 0f
            val current = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)?.toFloat() ?: 0f
            return AdjustInfo(min, max, current)
        }

        override fun changeSystemVolumeImpl(newVolume: Float) {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                newVolume.toInt(),
                AudioManager.FLAG_VIBRATE
            )
        }

        override fun getBrightnessInfo(): AdjustInfo {
            val current = (MediaLightUtils.getActivityBrightness(this@VideoPlayerActivity))
            return AdjustInfo(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL, current)
        }

        override fun changeBrightnessImpl(newBrightness: Float) {
            MediaLightUtils.setAppBrightness(newBrightness, this@VideoPlayerActivity)
        }
    }

    private val controlCallback = object : IControlCallback {
        override fun last() {
            play(-1, "No more files")
        }

        override fun next() {
            play(1, "No more files")
        }

        @SuppressLint("SourceLockedOrientationActivity")
        override fun landscape() {
            val currentOrientation = resources.configuration.orientation
            val activity: Activity = this@VideoPlayerActivity
            if (activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
        }

        @SuppressLint("SourceLockedOrientationActivity")
        override fun portrait() {
            val currentOrientation = resources.configuration.orientation
            val activity: Activity = this@VideoPlayerActivity
            if (activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
        }

        override fun onBeforeDropSeek() {
            this@VideoPlayerActivity.onBeforeDropSeek(true)
        }

        override fun onDroppingSeek(progress: Long) {
            this@VideoPlayerActivity.onDroppingSeek(progress)
        }

        override fun onAfterDropSeek() {
            this@VideoPlayerActivity.onAfterDropSeek()
        }
    }
    //----end----

    //---- 横竖屏处理 start---
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.onLandscape()
            videoControlController?.onLandscape()
            videoTouchController?.onLandscape()
        } else if (newConfig?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.onPortrait()
            videoControlController?.onPortrait()
            videoTouchController?.onPortrait()
        }
    }

    private val screenWidth by lazy { MediaScreenUtils.getScreenWidth(this) }

    private fun onLandscape() {
        val rate = playingInfo.videoWHRate

        val lp = textureView.layoutParams
        lp.height = screenWidth
        lp.width = (screenWidth.toFloat() * rate).toInt()
        textureView.layoutParams = lp
    }

    private fun onPortrait() {
        val rate = playingInfo.videoWHRate

        val lp = textureView.layoutParams
        lp.width = screenWidth
        lp.height = (screenWidth.toFloat() / rate).toInt()
        textureView.layoutParams = lp
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
}