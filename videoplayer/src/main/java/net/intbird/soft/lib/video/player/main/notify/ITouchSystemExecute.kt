package net.intbird.soft.lib.video.player.main.notify

import net.intbird.soft.lib.video.player.main.notify.mode.AdjustInfo

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface ITouchSystemExecute {

    fun getVolumeInfo() : AdjustInfo
    fun changeSystemVolumeImpl(newVolume: Float)

    fun getBrightnessInfo(): AdjustInfo
    fun changeBrightnessImpl(newBrightness: Float)
}
