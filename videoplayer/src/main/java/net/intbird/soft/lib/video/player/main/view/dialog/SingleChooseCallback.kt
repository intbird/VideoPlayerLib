package net.intbird.soft.lib.video.player.main.view.dialog

import net.intbird.soft.lib.video.player.api.bean.MediaCheckedData

/**
 * created by Bird
 * on 2020/9/11
 * DingTalk id: intbird
 */

interface SingleChooseCallback {
    fun onChooseItem(index: Int, mediaCheckedData: MediaCheckedData)
}