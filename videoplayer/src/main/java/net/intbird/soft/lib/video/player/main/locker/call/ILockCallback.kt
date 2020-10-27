package net.intbird.soft.lib.video.player.main.locker.call

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface ILockCallback {
    fun needLock()
    fun needUnLock(unLockImmediately: Boolean = false)
}