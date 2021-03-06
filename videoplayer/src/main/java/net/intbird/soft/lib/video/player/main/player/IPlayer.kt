package net.intbird.soft.lib.video.player.main.player;

import net.intbird.soft.lib.video.player.main.player.player.call.IParamsChange
import net.intbird.soft.lib.video.player.main.player.player.call.IParamsStateInfo
import net.intbird.soft.lib.video.player.main.player.player.call.IPayloadPlaylist
import net.intbird.soft.lib.video.player.main.player.mode.MediaFileInfo

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
interface IPlayer : IParamsStateInfo, IParamsChange, IPayloadPlaylist {

    fun prepare(mediaFileInfo: MediaFileInfo)

    fun start()

    fun seekTo(duration: Long, autoPlay: Boolean)

    fun resume()

    fun pause()

    fun last(): Boolean

    fun next(): Boolean

    fun stop()

    fun destroy()
}