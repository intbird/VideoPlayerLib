package intbird.soft.lib.video.player.main.simple

import androidx.fragment.app.Fragment
import intbird.soft.lib.video.player.api.state.IVideoPlayerCallback
import intbird.soft.lib.video.player.utils.MediaLogUtil

/**
 * created by intbird
 * on 2020/9/8
 * DingTalk id: intbird
 */
open class SimplePlayStateCallback : IVideoPlayerCallback {

    private val tag = "SimplePlayStateCallback"

    override fun onCreated(fragment: Fragment) {
        MediaLogUtil.log(tag + " onCreated")
    }

    override fun onPrepare() {
        MediaLogUtil.log(tag + " onPrepare")
    }

    override fun onPrepared() {
        MediaLogUtil.log(tag + " onPrepared")
    }

    override fun onStart() {
        MediaLogUtil.log(tag + " onStart")
    }

    override fun onSeekTo(progress: Long?) {
        MediaLogUtil.log(tag + " onSeekTo: $progress")
    }

    override fun onPause(progress: Long?) {
        MediaLogUtil.log(tag + " onPause: $progress")
    }

    override fun onCompletion() {
        MediaLogUtil.log(tag + " onCompletion")
    }

    override fun onStop() {
        MediaLogUtil.log(tag + " onStop")
    }

    override fun onError(errorCode: Int, errorMessage: String?) {
        MediaLogUtil.log(tag + " onError:$errorCode")
    }

    override fun onBuffStart() {
        MediaLogUtil.log(tag + " onBuffStart")
    }

    override fun onBuffEnded() {
        MediaLogUtil.log(tag + " onBuffEnded")
    }

}
