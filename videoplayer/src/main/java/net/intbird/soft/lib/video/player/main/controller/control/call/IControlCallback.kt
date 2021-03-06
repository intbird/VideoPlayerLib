package net.intbird.soft.lib.video.player.main.controller.control.call

import net.intbird.soft.lib.video.player.main.view.dialog.type.SingleChooseType
import net.intbird.soft.lib.video.player.main.notify.IMediaSeekNotify
/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IControlCallback :
        IMediaSeekNotify {

    fun showDialog(dialogType: SingleChooseType, show: Boolean)

    fun backward(long: Long)
    fun forward(long: Long)

    fun last():Boolean
    fun next():Boolean

    fun landscape()
    fun portrait()
}