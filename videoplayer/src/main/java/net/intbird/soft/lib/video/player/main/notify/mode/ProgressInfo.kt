package net.intbird.soft.lib.video.player.main.notify.mode

import net.intbird.soft.lib.video.player.utils.MediaTimeUtil

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
class ProgressInfo {
    var available: Boolean = false

    private var minValue = 0L
    var maxValue = 0L
    private var currentValue = 0L

    private var minValueUI = 0
    var maxValueUI = 0
    private var currentValueUI = 0

    var progress = 0L
    var progressUI = 0

    constructor() {
        this.available = false
    }

    constructor(minValue: Long, maxValue: Long, progressValue: Long) {
        this.available = true

        this.minValue = minValue
        this.maxValue = maxValue
        this.currentValue = progressValue

        absUIValue()
    }

    private fun absUIValue() {
        val actuary = 100
        if (minValue < 0) {
            val diff = 0 - minValue
            minValueUI += ((minValue + diff) * actuary).toInt()
            maxValueUI += ((maxValue + diff) * actuary).toInt()
            currentValueUI += ((currentValue + diff) * actuary).toInt()
        } else {
            minValueUI += (minValue * actuary).toInt()
            maxValueUI += (maxValue * actuary).toInt()
            currentValueUI += (currentValue * actuary).toInt()
        }
    }

    fun addIncrease(increaseRatio: Float) {
        progress = MediaTimeUtil.adjustValueBoundL((currentValue + increaseRatio * maxValue).toLong(), maxValue, minValue)
        progressUI = MediaTimeUtil.adjustValueBoundL((currentValueUI + (increaseRatio * maxValueUI)).toLong(), maxValueUI.toLong(), minValueUI.toLong()).toInt()
    }
}