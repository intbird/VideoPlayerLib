package intbird.soft.lib.video.player.main.locker

import android.os.Handler
import android.os.Looper
import android.view.View
import intbird.soft.lib.video.player.main.locker.call.ILockCallback
import intbird.soft.lib.video.player.main.notify.ILockExecute

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
class LockController(private val ivPopLock: View) : ILockCallback {

    private var locked = false

    private var handler = Handler(Looper.getMainLooper())
    private val dismissLockTime = 2000L

    private var dismissCallback = Runnable {
        ivPopLock.visibility = View.GONE
    }

    private val lockerExecutes = mutableListOf<ILockExecute>()

    init {
        ivPopLock.setOnClickListener { needUnLock(true) }
    }

    fun addExecute(iLockExecute: ILockExecute?): LockController {
        if (null != iLockExecute) {
            lockerExecutes.add(iLockExecute)
        }
        return this
    }

    fun notifyExecute(lock: Boolean) {
        for (lockDelegate in lockerExecutes.iterator()) {
            lockDelegate.executeLock(lock)
        }
    }

    private fun showLockView(visible: Boolean) {
        if (visible) {
            ivPopLock.visibility = View.VISIBLE
            handler.removeCallbacks(dismissCallback)
            handler.postDelayed(dismissCallback, dismissLockTime)
        } else {
            handler.removeCallbacks(dismissCallback)
            ivPopLock.visibility = View.GONE
        }
    }

    fun isLocked() = locked

    override fun needLock() {
        locked = true
        showLockView(true)
        notifyExecute(true)
    }

    override fun needUnLock(unLockImmediately: Boolean) {
        if (unLockImmediately) {
            locked = false
            showLockView(false)
            notifyExecute(false)
        } else {
            showLockView(true)
        }
    }

    fun destroy() {
        handler.removeCallbacks(dismissCallback)
        lockerExecutes.clear()
    }
}