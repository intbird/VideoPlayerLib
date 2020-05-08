package intbird.soft.lib.video.player.main.controller.control.call

import intbird.soft.lib.video.player.main.notify.IMediaSeekNotify
/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IControlCallback :
        IMediaSeekNotify {

    fun last()
    fun next()

    fun landscape()
    fun portrait()
}