package intbird.soft.lib.video.player.main

import android.content.Context
import android.content.Intent
import com.google.auto.service.AutoService
import intbird.soft.lib.video.player.api.IVideoPlayer
import intbird.soft.lib.video.player.api.bean.MediaClarity
import intbird.soft.lib.video.player.api.bean.MediaPlayItem
import intbird.soft.lib.video.player.main.view.MediaPlayerType

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
@AutoService(IVideoPlayer::class)
class VideoPlayerServiceImpl : IVideoPlayer {

    override fun startActivity(
        context: Context?,
        videoPaths: ArrayList<MediaPlayItem>?,
        index: Int
    ) {
        if (null == context) return
        val intentPlayer = Intent(context, VideoPlayerActivity::class.java)
        intentPlayer.putExtra(VideoPlayerFragmentLite.EXTRA_FILE_URLS, videoPaths)
        intentPlayer.putExtra(VideoPlayerFragmentLite.EXTRA_FILE_INDEX, index)
        intentPlayer.putExtra(
            VideoPlayerFragmentLite.EXTRA_PLAYER_TYPE,
            MediaPlayerType.PLAYER_STYLE_1
        )
        context.startActivity(intentPlayer)
    }

    override fun startActivity(context: Context?, videoPaths: Array<String>?, index: Int) {
        if (null == context) return
        val intentPlayer = Intent(context, VideoPlayerActivity::class.java)
        intentPlayer.putExtra(VideoPlayerFragmentLite.EXTRA_FILE_URLS, compatFileUrls(videoPaths))
        intentPlayer.putExtra(VideoPlayerFragmentLite.EXTRA_FILE_INDEX, index)
        intentPlayer.putExtra(
            VideoPlayerFragmentLite.EXTRA_PLAYER_TYPE,
            MediaPlayerType.PLAYER_STYLE_2
        )
        context.startActivity(intentPlayer)
    }

    private fun compatFileUrls(videoPaths: Array<String>?): ArrayList<MediaPlayItem>? {
        val mediaPlayItems = ArrayList<MediaPlayItem>()
        return if (null == videoPaths) {
            mediaPlayItems
        } else {
            for (videoPath in videoPaths) {
                mediaPlayItems.add(compatFileUrl(videoPath))
            }
            mediaPlayItems
        }
    }

    private fun compatFileUrl(videoPath: String): MediaPlayItem {
        return MediaPlayItem(
            "mediaId", "",
            arrayListOf(MediaClarity(clarity = "", mediaUrl = videoPath)),
            arrayListOf(),
            0
        )
    }
}