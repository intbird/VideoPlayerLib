package intbird.soft.lib.video.player.main.intent

import android.os.Bundle
import intbird.soft.lib.video.player.api.bean.MediaClarity
import intbird.soft.lib.video.player.api.bean.MediaPlayItem
import intbird.soft.lib.video.player.api.bean.MediaRate
import intbird.soft.lib.video.player.main.VideoPlayerFragmentLite
import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import intbird.soft.lib.video.player.utils.MediaFileUtils
import intbird.soft.lib.video.player.utils.MediaLogUtil

/**
 * created by intbird
 * on 2020/9/10
 * DingTalk id: intbird
 */
open class MediaIntentParser(
    private val arguments: Bundle?,
    private val intentCallback: IMediaIntentCallback?
) : MediaIntentDelegate {

    private var videoPlayItems: ArrayList<MediaPlayItem>? = null
    private var videoPlayIndex: Int = 0
    private var videoAutoPlay: Boolean? = null

    fun isAutoStartPlay(): Boolean  = videoAutoPlay == true

    fun clearAutoStartPlay() {
        videoAutoPlay = null
    }

    init {
        setVideoPlayerList(
            arguments?.getParcelableArrayList<MediaPlayItem>(VideoPlayerFragmentLite.EXTRA_FILE_URLS),
            arguments?.getInt(VideoPlayerFragmentLite.EXTRA_FILE_INDEX) ?: 0,
            arguments?.getBoolean(VideoPlayerFragmentLite.EXTRA_PLAYER_AUTO_PLAY) ?: true
        )
    }

    fun setVideoPlayerList(
        playList: ArrayList<MediaPlayItem>?,
        playIndex: Int,
        autoPlay: Boolean
    ): MediaIntentParser {
        videoPlayItems = playList
        videoPlayIndex = playIndex
        videoAutoPlay = autoPlay
        if (autoPlay) play(0)
        log("setVideoPlayerList: playList:$playList playIndex: $playIndex autoPlay: $autoPlay")
        return this
    }

    override fun delegatePlay(): Boolean {
        return play(0)
    }

    override fun delegateLast(): Boolean {
        return play(-1)
    }

    override fun delegateNext(): Boolean {
        return play(1)
    }

    private fun log(message: String) {
        MediaLogUtil.log(message)
    }

    private fun play(crease: Int): Boolean {
        val creasedFile = getCreasedFile(crease)
        log("play file: $creasedFile")
        return if (null != creasedFile) {
            videoPlayIndex = videoPlayIndex.plus(crease)
            intentCallback?.onReceivePlayFile(creasedFile)
            true
        } else {
            false
        }
    }

    private fun getCreasedFile(crease: Int): MediaFileInfo? {
        if (null == videoPlayItems) {
            return null
        }
        val newIndex = videoPlayIndex + crease
        if (newIndex < 0) {
            return null
        }
        if (newIndex > videoPlayItems!!.size - 1) {
            return null
        }
        log("ceasedFile index :$newIndex")
        val playItem = videoPlayItems!![newIndex]
        val mediaItemWrapper = getPlayerMediaItem(playItem)
        val support = MediaFileUtils.supportFileType(mediaItemWrapper.mediaClarity?.mediaUrl)
        log("support file :$support")
        if (!support) {
            return null
        }
        val mediaFileInfo = MediaFileInfo(
            playItem.mediaId,
            playItem.mediaName,
            mediaItemWrapper.mediaClarity?.mediaUrl,
            mediaItemWrapper.mediaClarity?.mediaHeaders,

            mediaItemWrapper.mediaClarity?.text,
            mediaItemWrapper.mediaRate?.rate
        )
        log("MediaFileInfo:$mediaFileInfo")
        return mediaFileInfo
    }

    data class PlayerMediaItemWrapper(var mediaClarity: MediaClarity?, var mediaRate: MediaRate?)

    var playingProgress: Long? = 0L
    var playingItem: MediaPlayItem? = null
    var playingChild: PlayerMediaItemWrapper? = null

    private fun getPlayerMediaItem(playItem: MediaPlayItem): PlayerMediaItemWrapper {
        log("mediaItem: $playItem")
        this.playingItem = playItem
        val clarityConfig = playItem.clarityArray
        log("mediaItem-Config: $clarityConfig")
        this.playingChild = PlayerMediaItemWrapper(
            getPlayerMediaClarityItem(playItem),
            getPlayerMediaRateItem(playItem)
        )
        log("mediaItem-Config-Item: $playingChild")
        return playingChild!!
    }

    private fun getPlayerMediaClarityItem(playItem: MediaPlayItem): MediaClarity? {
        val list = playItem.clarityArray ?: ArrayList()
        val index = getSelectedClarity(list)
        return if (index in 0 until list.size) {
            list[index]
        } else null
    }

    private fun getSelectedClarity(list: ArrayList<MediaClarity>): Int {
        return if (null != this.playingChild) {
            list.indexOf(playingChild?.mediaClarity)
        } else {
            for (item in list) {
                return if (item.checked) list.indexOf(item) else continue
            };-1
        }
    }

    fun playSelectedClarity(progress: Long, mediaClarity: MediaClarity): Boolean {
        this.playingProgress = progress
        this.playingChild?.mediaClarity = mediaClarity
        return delegatePlay()
    }

    private fun getPlayerMediaRateItem(playItem: MediaPlayItem): MediaRate? {
        val list = playItem.rateArray ?: ArrayList()
        val index = getSelectRate(list)
        return if (index in 0 until list.size){
            list[index]
        } else null
    }

    private fun getSelectRate(list: ArrayList<MediaRate>): Int {
        return if (null != this.playingChild) {
            list.indexOf(playingChild?.mediaRate)
        } else {
            for (item in list) {
                return if (item.checked) list.indexOf(item) else continue
            };-1
        }
    }

    fun playSelectedRate(progress: Long, mediaRate: MediaRate): Boolean {
        this.playingProgress = progress
        this.playingChild?.mediaRate = mediaRate
        return delegatePlay()
    }
}