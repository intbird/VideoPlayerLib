package intbird.soft.app

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import intbird.soft.lib.service.loader.ServicesLoader
import intbird.soft.lib.video.player.api.IVideoPlayer
import intbird.soft.lib.video.player.api.IVideoPlayerCallback
import intbird.soft.lib.video.player.api.bean.MediaClarity
import intbird.soft.lib.video.player.api.bean.MediaPlayItem
import intbird.soft.lib.video.player.api.bean.MediaPlayerStyle
import intbird.soft.lib.video.player.main.VideoPlayerFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val itemTestIndex = 4

    val itemTestUrl1 = "file:///sdcard/videos/test1.mp4"
    val itemTestUrl2 = "file:///sdcard/videos/test2.mp4"
    val itemTestUrl3 = "file:///sdcard/videos/test3.mp4"
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
        "4", "fileName4", arrayListOf(
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

        // 自定义高度和样式播放器
        add1.setOnClickListener { addVideoPlayer(R.id.fragment_player, MediaPlayerStyle.SHOW_LAST_NEXT) }
        add2.setOnClickListener { addVideoPlayer(R.id.fragment_player, MediaPlayerStyle.SHOW_BACKWARD_FORWARD) }
        remove.setOnClickListener { removeAudioPlayer(R.id.fragment_player) }

        last.setOnClickListener { fragment?.setVideoPlayerLast() }
        pause.setOnClickListener { fragment?.getVideoPlayerController()?.pause() }
        next.setOnClickListener { fragment?.setVideoPlayerNext() }


        fullScreen1.setOnClickListener {
            // 全屏播放 : 底部 快进/快退 MediaPlayerStyle.HIDE_LAST_NEXT
            ServicesLoader.load(IVideoPlayer::class.java)?.startActivity(this, arrayListOf(itemTest1, itemTest2, itemTest3, itemTest4, itemTest5), itemTestIndex)
        }

        fullScreen2.setOnClickListener {
            // 全屏播放 : 底部 上一个/下一个 MediaPlayerStyle.HIDE_BACKWARD_FORWARD
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

    private fun addVideoPlayer(rid: Int, style: MediaPlayerStyle) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = VideoPlayerFragment()
        setFragmentArgs(fragment, style)
        fragmentTransaction.add(rid, fragment)
        fragmentTransaction.commit()
        this.fragment = fragment
    }

    private fun removeAudioPlayer(rid:Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentManager.findFragmentById(rid)?.let { fragmentTransaction.remove(it) }
        fragmentTransaction.commit()
    }

    private fun setFragmentArgs(fragment: VideoPlayerFragment, style: MediaPlayerStyle) {
        var args = fragment.arguments
        if (null == args) args = Bundle()
        args.putParcelableArrayList(VideoPlayerFragment.EXTRA_FILE_URLS, arrayListOf(itemTest1, itemTest2, itemTest3, itemTest4, itemTest5))
        args.putInt(VideoPlayerFragment.EXTRA_FILE_INDEX, itemTestIndex)
        args.putSerializable(VideoPlayerFragment.EXTRA_PLAYER_STYLE, style)
        fragment.arguments = args

        fragment.registerPlayStateCallback(ReceivePlayerState(this))
    }

    inner class ReceivePlayerState(val context: Context) : IVideoPlayerCallback {
        override fun onCreated(fragment: Fragment) {
            // 动态数据切换
            if (fragment is VideoPlayerFragment) {
                fragment.setVideoPlayerList(arrayListOf(itemTest1, itemTest2, itemTest3, itemTest4, itemTest5), itemTestIndex)
                fragment.setVideoPlayerLast()
                fragment.setVideoPlayerNext()

                fragment.getVideoPlayingInfo()
                fragment.getVideoPlayerController()?.getTotalTime()
            }
        }

        override fun onPrepare() {
            Toast.makeText(context, "MainActivity: onPrepare", Toast.LENGTH_SHORT).show()
        }

        override fun onPrepared() {
            Toast.makeText(context, "MainActivity: onPrepared", Toast.LENGTH_SHORT).show()
        }

        override fun onStart() {
            Toast.makeText(context, "MainActivity: onStart", Toast.LENGTH_SHORT).show()
        }

        override fun onSeekTo(progress: Long?) {
            Toast.makeText(context, "MainActivity: onSeekTo: $progress", Toast.LENGTH_SHORT).show()
        }

        override fun onPause(progress: Long?) {
            Toast.makeText(context, "MainActivity: onPause: $progress", Toast.LENGTH_SHORT).show()
        }

        override fun onCompletion() {
            Toast.makeText(context, "MainActivity: onCompletion", Toast.LENGTH_SHORT).show()
        }

        override fun onStop() {
            Toast.makeText(context, "MainActivity: onStop", Toast.LENGTH_SHORT).show()
        }

        override fun onError(errorMessage: String?) {
            Toast.makeText(context, "MainActivity: onError", Toast.LENGTH_SHORT).show()
        }
    }
}
