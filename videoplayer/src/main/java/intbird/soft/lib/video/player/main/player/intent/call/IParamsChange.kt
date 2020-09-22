package intbird.soft.lib.video.player.main.player.intent.call

import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo

interface IParamsChange {
    fun onParamsChange(mediaFileInfo: MediaFileInfo?)
}