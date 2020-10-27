package net.intbird.soft.lib.video.player.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import net.intbird.soft.lib.video.player.R

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 *
 */
open class VideoPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lib_media_player_activity)
        addVideoPlayer(R.id.fragment_player)
    }

    open var videoPlayer: VideoPlayerFragment? = null

    private fun addVideoPlayer(rid: Int) {
        removeAudioPlayer(rid)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = VideoPlayerFragment()
        fragment.arguments = intent.extras
        fragmentTransaction.add(rid, fragment)
        fragmentTransaction.commit()
        this.videoPlayer = fragment
    }

    private fun removeAudioPlayer(rid: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentManager.findFragmentById(rid)?.let { fragmentTransaction.remove(it) }
        fragmentTransaction.commit()
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
        actionBar?.hide()
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        actionBar?.show()
    }

//    private fun setFragmentArgs(fragment: Fragment) {
//        var args = fragment.arguments
//        if (null == args) args = Bundle()
//        args.putParcelableArrayList(
//            VideoPlayerFragment.EXTRA_FILE_URLS,
//            intent.getParcelableArrayListExtra(VideoPlayerFragment.EXTRA_FILE_URLS)
//        )
//        args.putInt(
//            VideoPlayerFragment.EXTRA_FILE_INDEX,
//            intent.getIntExtra(VideoPlayerFragment.EXTRA_FILE_INDEX, 0)
//        )
//        args.putSerializable(
//            VideoPlayerFragment.EXTRA_PLAYER_TYPE,
//            intent.getSerializableExtra(VideoPlayerFragment.EXTRA_PLAYER_TYPE)
//        )
//        args.putSerializable(
//            VideoPlayerFragment.EXTRA_PLAYER_AUTO_PLAY,
//            intent.getSerializableExtra(VideoPlayerFragment.EXTRA_PLAYER_AUTO_PLAY)
//        )
//    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            // fragment_player.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
//            // fragment_player.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            // fragment_player.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
//            // fragment_player.layoutParams.height =  fragment_player.context.resources.getDimension(R.dimen.lib_media_playerVideoPlayerHeight).toInt()
//        }
//    }
}