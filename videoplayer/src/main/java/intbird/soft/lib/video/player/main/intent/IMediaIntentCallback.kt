package intbird.soft.lib.video.player.main.intent

import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo

/**
 * created by intbird
 * on 2020/9/10
 * DingTalk id: intbird
 */

interface IMediaIntentCallback {
    fun onReceivePlayFile(mediaFileInfo: MediaFileInfo)
}