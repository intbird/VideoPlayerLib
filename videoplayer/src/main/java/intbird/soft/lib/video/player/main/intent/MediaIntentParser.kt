package intbird.soft.lib.video.player.main.intent

import android.os.Bundle
import intbird.soft.lib.video.player.api.bean.MediaClarity
import intbird.soft.lib.video.player.api.bean.MediaPlayItem
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

    private var playingItem: MediaPlayItem? = null
    private var playingItemChild: MediaClarity? = null

    fun getPlayingItem() = playingItem
    fun getPlayingItemChild() = playingItemChild

    fun isAutoStartPlay(): Boolean = videoAutoPlay == true

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
        val mediaItem = getPlayerMediaItem(playItem)
        val support = MediaFileUtils.supportFileType(mediaItem.mediaUrl)
        log("support file :$support")
        if (!support) {
            return null
        }
        val mediaFileInfo = MediaFileInfo(
            playItem.mediaId,
            playItem.mediaName,
            mediaItem.mediaUrl,
            mediaItem.mediaHeaders,
            mediaItem.clarityText
        )
        log("MediaFileInfo:$mediaFileInfo")
        return mediaFileInfo
    }

    private fun getPlayerMediaItem(playItem: MediaPlayItem): MediaClarity {
        log("mediaItem: $playItem")

        this.playingItem = playItem
        val clarityConfig = playItem.mediaConfig
        intentCallback?.onReceivePlaylist(clarityConfig)
        log("mediaItem-Config: $clarityConfig")

        val clarityConfigItem = getPlayerMediaConfigItem(playItem)
        this.playingItemChild = clarityConfigItem
        intentCallback?.onReceivePlayItem(clarityConfigItem)
        log("mediaItem-Config-Item: $clarityConfigItem")
        return clarityConfigItem
    }

    private fun getPlayerMediaConfigItem(playItem: MediaPlayItem): MediaClarity {
        val checkedMediaClarity = intentCallback?.getLastCheckedPlay()
        var index = if (null != checkedMediaClarity
            && checkedMediaClarity.selectedByUser
            && checkedMediaClarity.mediaId == playItem.mediaId
        ) {
            checkedMediaClarity.clarityIndex
        } else {
            playItem.defaultSelected ?: 0
        }
        if (index !in 0 until playItem.mediaConfig.size) index = 0
        val configItem = playItem.mediaConfig[index]
        configItem.selectedByUser = false
        configItem.clarityProgress =
            checkedMediaClarity?.clarityProgress ?: (playItem.defaultProgress ?: 0)
        return configItem
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
}