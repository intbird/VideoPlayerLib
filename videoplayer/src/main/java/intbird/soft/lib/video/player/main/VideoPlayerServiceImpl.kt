package intbird.soft.lib.video.player.main

import android.content.Context
import android.content.Intent
import com.google.auto.service.AutoService
import intbird.soft.lib.video.player.api.IVideoPlayer

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
@AutoService(IVideoPlayer::class)
class VideoPlayerServiceImpl : IVideoPlayer {
    override fun startActivity(context: Context?, videoPaths: Array<String>?, index: Int) {
        if (null == context) return
        val intentPlayer = Intent(context, VideoPlayerActivity::class.java)
        intentPlayer.putExtra(VideoPlayerActivity.EXTRA_FILE_URLS, videoPaths)
        intentPlayer.putExtra(VideoPlayerActivity.EXTRA_FILE_INDEX, index)
        context.startActivity(intentPlayer)
    }
}