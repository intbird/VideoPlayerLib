package intbird.soft.lib.video.player.main.player.intent.delegate

/**
 * created by intbird
 * on 2020/9/10
 * DingTalk id: intbird
 *
 * last and next all in items,so the player doest not impl
 */
interface PlayerDelegate {
    fun delegatePlay(): Boolean
    fun delegateLast(): Boolean
    fun delegateNext(): Boolean
}