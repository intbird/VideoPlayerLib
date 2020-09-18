package intbird.soft.lib.video.player.main.dialog.type

import intbird.soft.lib.video.player.api.bean.MediaCheckedData

/**
 * created by Bird
 * on 2020/9/11
 * DingTalk id: intbird
 */
interface SingleChooseTypeCallback {
    fun onCreateItem(type: SingleChooseType): ArrayList<out MediaCheckedData>?
    fun onChooseItem(type: SingleChooseType, index: Int, mediaCheckedData: MediaCheckedData, play:Boolean)
}