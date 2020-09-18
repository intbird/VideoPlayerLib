package intbird.soft.lib.video.player.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import intbird.soft.lib.video.player.R

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 *
 * 时间有限
 */
open class VideoPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lib_media_player_activity)

        addVideoPlayer(R.id.fragment_player)
    }

    private var videoPlayerFragment: VideoPlayerFragment? = null

    fun getVideoPlayer(): VideoPlayerFragment? {
        return videoPlayerFragment
    }

    private fun addVideoPlayer(rid: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = VideoPlayerFragment()
        setFragmentArgs(fragment)
        fragmentTransaction.add(rid, fragment)
        fragmentTransaction.commit()
        this.videoPlayerFragment = fragment
    }

    private fun setFragmentArgs(fragment: Fragment) {
        var args = fragment.arguments
        if (null == args) args = Bundle()
        args.putParcelableArrayList(
            VideoPlayerFragment.EXTRA_FILE_URLS,
            intent.getParcelableArrayListExtra(VideoPlayerFragment.EXTRA_FILE_URLS)
        )
        args.putInt(
            VideoPlayerFragment.EXTRA_FILE_INDEX,
            intent.getIntExtra(VideoPlayerFragment.EXTRA_FILE_INDEX, 0)
        )
        args.putSerializable(
            VideoPlayerFragment.EXTRA_PLAYER_TYPE,
            intent.getSerializableExtra(VideoPlayerFragment.EXTRA_PLAYER_TYPE)
        )
        fragment.arguments = args
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