package intbird.soft.lib.video.player.main.player.call

import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
class PlayerCallbacks(vararg callbackVars: IPlayerCallback) : IPlayerCallback {

    private val callbacks = mutableListOf<IPlayerCallback>()

    init {
        for (playerCallback in callbackVars) {
            callbacks.add(playerCallback)
        }
    }

    fun addCallback(iPlayerCallback: IPlayerCallback): PlayerCallbacks {
        callbacks.add(iPlayerCallback)
        return this
    }

    fun removeCallback(iPlayerCallback: IPlayerCallback): PlayerCallbacks {
        callbacks.remove(iPlayerCallback)
        return this
    }

    override fun onPrepare(mediaFileInfo: MediaFileInfo) {
        for (callback in callbacks) {
            callback.onPrepare(mediaFileInfo)
        }
    }

    override fun onPrepared(mediaFileInfo: MediaFileInfo) {
        for (callback in callbacks) {
            callback.onPrepared(mediaFileInfo)
        }
    }

    override fun onStart() {
        for (callback in callbacks) {
            callback.onStart()
        }
    }

    override fun onSeekTo(duration: Long) {
        for (callback in callbacks) {
            callback.onSeekTo(duration)
        }
    }

    override fun onPause() {
        for (callback in callbacks) {
            callback.onPause()
        }
    }

    override fun onCompletion(mediaFileInfo: MediaFileInfo) {
        for (callback in callbacks) {
            callback.onCompletion(mediaFileInfo)
        }
    }

    override fun onStop() {
        for (callback in callbacks) {
            callback.onStop()
        }
    }

    override fun onError(errorMessage: String?) {
        for (callback in callbacks) {
            callback.onError(errorMessage)
        }
    }

    override fun onVideoSizeChanged(mediaFileInfo: MediaFileInfo) {
        for (callback in callbacks) {
            callback.onVideoSizeChanged(mediaFileInfo)
        }
    }
}