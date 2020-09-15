package intbird.soft.lib.video.player.main.intent

/**
 * created by intbird
 * on 2020/9/10
 * DingTalk id: intbird
 */

interface MediaIntentDelegate {
    fun delegatePlay():Boolean
    fun delegateLast():Boolean
    fun delegateNext():Boolean
}