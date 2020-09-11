package intbird.soft.app

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import intbird.soft.lib.service.loader.ServicesLoader
import intbird.soft.lib.video.player.api.IVideoPlayer
import intbird.soft.lib.video.player.api.bean.MediaClarity
import intbird.soft.lib.video.player.api.bean.MediaPlayItem
import intbird.soft.lib.video.player.api.state.IVideoPlayerCallback
import intbird.soft.lib.video.player.main.VideoPlayerFragment
import intbird.soft.lib.video.player.main.view.MediaPlayerType
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val itemTestIndex = 4

    val itemTestUrl1 = "file:///sdcard/videos/Instagram_0312_10_19_20.mp4"
    val itemTestUrl2 = "file:///sdcard/videos/My_Feed_on_Vimeo_0323_14_40_13.mp4"
    val itemTestUrl3 = "file:///sdcard/videos/tiktok_0409_10_55_07.mp4"
    val itemTestUrl4 = "https://intbird.s3.ap-northeast-2.amazonaws.com/476426784_mp4_h264_aac_hq.m3u8"
    val itemTestUrl5 = "https://llvod.mxplay.com/video/d89b306af415d293a66a74a26c560ab5/2/hls/h264_baseline.m3u8"

    var itemTest1 = MediaPlayItem(
        "1", "fileName1", arrayListOf(
            MediaClarity(0, "1", "360P", itemTestUrl1),
            MediaClarity(1, "1", "720P", itemTestUrl1),
            MediaClarity(2, "1", "1080P", itemTestUrl1),
            MediaClarity(3, "1", "2K", itemTestUrl1),
            MediaClarity(4, "1", "4K", itemTestUrl1)
        ), 1, TimeUnit.SECONDS.toMillis(1)
    )
    var itemTest2 = MediaPlayItem(
        "2", "fileName2", arrayListOf(
            MediaClarity(0, "2", "360P", itemTestUrl2),
            MediaClarity(1, "2", "720P", itemTestUrl2),
            MediaClarity(2, "2", "1080P", itemTestUrl2),
            MediaClarity(3, "2", "2K", itemTestUrl2),
            MediaClarity(4, "2", "4K", itemTestUrl2)
        ), 2, TimeUnit.SECONDS.toMillis(10)
    )
    var itemTest3 = MediaPlayItem(
        "3", "fileName3", arrayListOf(
            MediaClarity(0, "3", "360P", itemTestUrl3),
            MediaClarity(1, "3", "720P", itemTestUrl3),
            MediaClarity(2, "3", "1080P", itemTestUrl3),
            MediaClarity(3, "3", "2K", itemTestUrl3),
            MediaClarity(4, "3", "4K", itemTestUrl3)
        ), 3, TimeUnit.SECONDS.toMillis(30)
    )
    var itemTest4 = MediaPlayItem(
        "4", "fileName4", arrayListOf(
            MediaClarity(0, "4", "360P", itemTestUrl4),
            MediaClarity(1, "4", "720P", itemTestUrl4),
            MediaClarity(2, "4", "1080P", itemTestUrl4),
            MediaClarity(3, "4", "2K", itemTestUrl1),
            MediaClarity(4, "4", "4K", itemTestUrl2)
        ), 3, TimeUnit.SECONDS.toMillis(5)
    )

    var itemTest5 = MediaPlayItem(
        "5", "fileName5", arrayListOf(
            MediaClarity(0, "4", "360P", itemTestUrl5, mapOf("Key" to "value")),
            MediaClarity(1, "4", "720P", itemTestUrl5),
            MediaClarity(2, "4", "1080P", itemTestUrl5),
            MediaClarity(3, "4", "2K", itemTestUrl5),
            MediaClarity(4, "4", "4K", itemTestUrl5)
        ), 3, TimeUnit.SECONDS.toMillis(5)
    )

    private var fragment: VideoPlayerFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // use as a fragment
        add1.setOnClickListener { addVideoPlayer(R.id.fragment_player, MediaPlayerType.PLAYER_STYLE_1)}
        add2.setOnClickListener { addVideoPlayer(R.id.fragment_player, MediaPlayerType.PLAYER_STYLE_2)}
        remove.setOnClickListener { removeAudioPlayer(R.id.fragment_player) }

        last.setOnClickListener { fragment?.getVideoPlayerController()?.last() }
        pause.setOnClickListener { fragment?.getVideoPlayerController()?.pause() }
        next.setOnClickListener { fragment?.getVideoPlayerController()?.next() }
        info.setOnClickListener { stateText.text = "info:${fragment?.getVideoPlayerStateInfo()?.getVideoPlayingItemChild()}" }

        // full screen :  MediaPlayerStyle.HIDE_LAST_NEXT
        fullScreen1.setOnClickListener {
            ServicesLoader.load(IVideoPlayer::class.java)?.startActivity(this, arrayListOf(itemTest1, itemTest2, itemTest3, itemTest4, itemTest5), itemTestIndex)
        }

        // full screen :  MediaPlayerStyle.HIDE_BACKWARD_FORWARD
        fullScreen2.setOnClickListener {
            ServicesLoader.load(IVideoPlayer::class.java)?.startActivity(this, arrayOf(itemTestUrl1, itemTestUrl2, itemTestUrl3, itemTestUrl4, itemTestUrl5), itemTestIndex)
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fragment_player.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            fragment_player.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            fragment_player.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            fragment_player.layoutParams.height =  fragment_player.context.resources.getDimension(R.dimen.lib_media_playerVideoPlayerHeight).toInt()
        }
    }

    private fun addVideoPlayer(rid: Int, type: MediaPlayerType) {
        removeAudioPlayer(rid)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = VideoPlayerFragment.newInstance(arrayListOf(itemTest1, itemTest2, itemTest3, itemTest4, itemTest5),itemTestIndex, type, autoPlay = true)
        fragmentTransaction.add(rid, fragment)
        fragmentTransaction.commit()

        this.fragment = fragment
        this.fragment?.setPlayerStateCallback(ReceivePlayerState(this))
    }

    private fun removeAudioPlayer(rid:Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentManager.findFragmentById(rid)?.let { fragmentTransaction.remove(it) }
        fragmentTransaction.commit()
    }

    inner class ReceivePlayerState(val context: Context) :
        IVideoPlayerCallback {
        override fun onCreated(fragment: Fragment) {
            // 动态数据切换
            if (fragment is VideoPlayerFragment) {
                //fragment.setVideoPlayerList(arrayListOf(itemTest1, itemTest2, itemTest3, itemTest4, itemTest5), itemTestIndex, autoPlay = true)
            }
        }

        override fun onPrepare() {
            stateText.text  = "MainActivity: onPrepare loading... \n\n ${fragment?.getVideoPlayerStateInfo()?.getVideoPlayingItemChild()}"
        }

        override fun onPrepared() {
            stateText.text  = "MainActivity: onPrepared"
        }

        override fun onStart() {
            stateText.text  = "MainActivity: onStart \n\n ${fragment?.getVideoPlayerStateInfo()?.getVideoPlayingItemChild()}"
        }

        override fun onSeekTo(progress: Long?) {
            stateText.text  = "MainActivity: onSeekTo:$progress"
        }

        override fun onPause(progress: Long?) {
            stateText.text  = "MainActivity: onPause: ${fragment?.getVideoPlayerStateInfo()?.getCurrentTime()}"
        }

        override fun onCompletion() {
            stateText.text  = "MainActivity: onCompletion"
        }

        override fun onStop() {
            stateText.text  = "MainActivity: onStop"
        }

        override fun onError(errorCode: Int, errorMessage: String?) {
            stateText.text  = "MainActivity: onError: $errorCode  = $errorMessage"
        }

        override fun onBuffStart() {
            stateText.text  = "MainActivity: onBuffStart"
        }

        override fun onBuffEnded() {
            stateText.text  = "MainActivity: onBuffEnded"
        }
    }
}
