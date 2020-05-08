package intbird.soft.lib.video.player.main.notify
/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IMediaSeekNotify {
    fun onBeforeDropSeek()
    fun onDroppingSeek(progress: Long)
    fun onAfterDropSeek()
}
