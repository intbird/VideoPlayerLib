package intbird.soft.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
//import intbird.soft.lib.service.loader.ServicesLoader
import intbird.soft.lib.video.player.api.IVideoPlayer
import intbird.soft.lib.video.player.main.VideoPlayerActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun click(view: View) {
        val path = "sdcard/My_Feed_on_Vimeo_0324_17_40_24.mp4"
        //ServicesLoader.load(IVideoPlayer::class.java)?.startActivity(this, arrayOf("path1", "path2"), 0)
        startActivity(this, arrayOf(path), 0)
    }

    fun startActivity(context: Context?, videoPaths: Array<String>?, index: Int) {
        if (null == context) return
        val intentPlayer = Intent(context, VideoPlayerActivity::class.java)
        intentPlayer.putExtra(VideoPlayerActivity.EXTRA_FILE_URLS, videoPaths)
        intentPlayer.putExtra(VideoPlayerActivity.EXTRA_FILE_INDEX, index)
        context.startActivity(intentPlayer)
    }

}
