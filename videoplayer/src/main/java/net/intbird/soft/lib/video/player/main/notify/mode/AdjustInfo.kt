package net.intbird.soft.lib.video.player.main.notify.mode

import net.intbird.soft.lib.video.player.utils.MediaTimeUtil

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
class AdjustInfo {
    var available: Boolean = false

    private var minValue = 0f
    var maxValue = 0f
    private var currentValue = 0f

    private var minValueUI = 0
    var maxValueUI = 0
    private var currentValueUI = 0

    var progress = 0f
    var progressUI = 0

    constructor() {
        this.available = false
    }

    constructor(minValue: Float, maxValue: Float, progressValue: Float) {
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
        progress = MediaTimeUtil.adjustValueBoundF((currentValue + increaseRatio * maxValue), maxValue, minValue)
        progressUI = MediaTimeUtil.adjustValueBoundF((currentValueUI + (increaseRatio * maxValueUI)), maxValueUI.toFloat(), minValueUI.toFloat()).toInt()
    }

    fun getUIRate(): Double {
        return progressUI.toDouble() / maxValueUI.toDouble()
    }
}