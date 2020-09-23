package intbird.soft.lib.video.player.main.player.intent.call

/**
 * created by intbird
 * on 2020/9/10
 * DingTalk id: intbird
 **/
interface IParamsStateInfo {

    fun isPlaying(): Boolean

    fun getCurrentTime(): Long

    fun getTotalTime(): Long
}