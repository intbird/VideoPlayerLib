package net.intbird.soft.lib.video.player.main.controller.touch.call

import net.intbird.soft.lib.video.player.main.notify.IMediaSeekNotify
import net.intbird.soft.lib.video.player.main.notify.ITouchSystemExecute

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IVideoTouchCallback : IMediaSeekNotify, ITouchSystemExecute {

    fun onSingleTap()
    fun onDoubleTap()
}