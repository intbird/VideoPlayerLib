package intbird.soft.lib.video.player.api

import android.content.Context

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IVideoPlayer {
    fun startActivity(
        context: Context?,
        videoPaths: Array<String>?,
        index: Int
    )
}