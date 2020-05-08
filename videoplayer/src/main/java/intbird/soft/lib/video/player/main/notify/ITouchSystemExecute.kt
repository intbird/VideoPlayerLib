package intbird.soft.lib.video.player.main.notify

import android.content.Context
import intbird.soft.lib.video.player.main.notify.mode.AdjustInfo

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface ITouchSystemExecute {

    fun getContext(): Context

    fun getVolumeInfo() : AdjustInfo
    fun changeSystemVolumeImpl(newVolume: Float)

    fun getBrightnessInfo(): AdjustInfo
    fun changeBrightnessImpl(newBrightness: Float)
}
