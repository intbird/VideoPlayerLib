package net.intbird.soft.lib.video.player.utils

import androidx.annotation.CheckResult
import java.util.concurrent.TimeUnit

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
object MediaTimeUtil {
    fun formatTime(milliseconds: Long): String? {
        if (milliseconds <= 0) return "00:00"
        val absSeconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        val h = absSeconds / 3600
        val m = (absSeconds % 3600) / 60
        val s = absSeconds % 60
        return if (h > 0) String.format("%02d:%02d:%02d", h, m, s)
        else String.format("%02d:%02d", m, s)
    }

    @CheckResult
    fun adjustValueBoundL(value: Long, maxValue: Long, minValue: Long = 0): Long {
        return when {
            value > maxValue -> maxValue
            value < minValue -> minValue
            else -> value
        }
    }

    @CheckResult
    fun adjustValueBoundF(value: Float, maxValue: Float, minValue: Float = 0f): Float {
        return when {
            value > maxValue -> maxValue
            value < minValue -> minValue
            else -> value
        }
    }
}