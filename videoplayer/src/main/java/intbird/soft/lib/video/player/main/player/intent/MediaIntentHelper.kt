package intbird.soft.lib.video.player.main.player.intent

import android.os.Bundle
import android.text.TextUtils
import intbird.soft.lib.video.player.api.bean.*
import intbird.soft.lib.video.player.main.VideoPlayerFragment
import intbird.soft.lib.video.player.main.dialog.type.SingleChooseType
import intbird.soft.lib.video.player.main.dialog.type.SingleChooseTypeCallback
import intbird.soft.lib.video.player.main.player.intent.parser.MediaItemInfoParer
import intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import intbird.soft.lib.video.player.main.player.player.delegate.PlayerDelegate
import intbird.soft.lib.video.player.main.view.MediaPlayerType
import intbird.soft.lib.video.player.utils.MediaFileUtils
import intbird.soft.lib.video.player.utils.MediaLogUtil

/**
 * created by intbird
 * on 2020/9/10
 * DingTalk id: intbird
 */
class MediaIntentHelper(
    private val arguments: Bundle?,
    private val intentHelperCall: MediaIntentHelperCall?
) : PlayerDelegate {

    interface MediaIntentHelperCall {
        fun getVideoCurrentTime(): Long?
        fun onReceivePlayFile(autoPlay: Boolean, mediaFileInfo: MediaFileInfo?)
    }

    private var videoPlayItems: ArrayList<MediaPlayItem>? = null
    private var videoPlayIndex: Int = 0
    private var videoAutoPlay: Boolean? = null

    private var lastPlayingId: String? = ""
    private var lastPlayingIdProgress: Long? = 0L

    val getMediaPlayerType get() = arguments?.getSerializable(VideoPlayerFragment.EXTRA_PLAYER_TYPE) as? MediaPlayerType
    val isAutoStartPlay get() = (videoAutoPlay == true)
    val lastPlayingProgress get() = lastPlayingIdProgress

    private var mediaItemParser = MediaItemInfoParer()

    init {
        setVideoPlayerList(
            arguments?.getParcelableArrayList<MediaPlayItem>(VideoPlayerFragment.EXTRA_FILE_URLS),
            arguments?.getInt(VideoPlayerFragment.EXTRA_FILE_INDEX) ?: 0,
            arguments?.getBoolean(VideoPlayerFragment.EXTRA_PLAYER_AUTO_PLAY) ?: true
        )
    }

    fun setVideoPlayerList(
        playList: ArrayList<MediaPlayItem>?,
        playIndex: Int,
        autoPlay: Boolean
    ): MediaIntentHelper {
        videoPlayItems = playList
        videoPlayIndex = playIndex
        videoAutoPlay = autoPlay
        reloadPlayer(autoPlay, 0)
        log("setVideoPlayerList: playList:$playList playIndex: $playIndex autoPlay: $autoPlay")
        return this
    }

    fun setVideoPlayerItem(mediaPlayItem: MediaPlayItem?, autoPlay: Boolean) {
        reloadPlayer(autoPlay, mediaPlayItem)
        log("setVideoPlayerItem-fileInfo:$mediaPlayItem")
    }

    fun setVideoPlayerItemInfo(mediaPlayItemInfo: MediaPlayItemInfo?, autoPlay: Boolean) {
        reloadPlayer(autoPlay, mediaPlayItemInfo)
        log("setVideoPlayerItem-fileInfo:$mediaPlayItemInfo")
    }

    override fun delegatePlay(): Boolean {
        return reloadPlayer(true, 0)
    }

    override fun delegateLast(): Boolean {
        return reloadPlayer(true, -1)
    }

    override fun delegateNext(): Boolean {
        return reloadPlayer(true, 1)
    }

    private fun reloadPlayer(autoPlay: Boolean, mediaPlayItem: MediaPlayItem?): Boolean {
        val fileInfo = parserPlayMediaItemInfo(mediaPlayItem)
        lastPlayingId = mediaPlayItem?.mediaId
        lastPlayingIdProgress = intentHelperCall?.getVideoCurrentTime()
        intentHelperCall?.onReceivePlayFile(autoPlay, fileInfo)
        return null != fileInfo
    }

    private fun reloadPlayer(autoPlay: Boolean, mediaPlayItemInfo: MediaPlayItemInfo?): Boolean {
        val fileInfo = parserPlayMediaItemInfo(mediaPlayItemInfo)
        lastPlayingId = mediaPlayItemInfo?.mediaId
        lastPlayingIdProgress = intentHelperCall?.getVideoCurrentTime()
        intentHelperCall?.onReceivePlayFile(autoPlay, fileInfo)
        return null != fileInfo
    }

    private fun reloadPlayer(autoPlay: Boolean, crease: Int): Boolean {
        val creasedFile = getCreasedFile(crease)
        log("play file: $creasedFile")
        return if (null != creasedFile) {
            videoPlayIndex = videoPlayIndex.plus(crease)
            intentHelperCall?.onReceivePlayFile(autoPlay, creasedFile)
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
        log("ceasedFile playItem :$playItem")
        val fileInfo = parserPlayMediaItemInfo(playItem)

        if(null != lastPlayingId
            && !TextUtils.equals(playItem.mediaId, lastPlayingId)) {
            lastPlayingId = playItem.mediaId
            lastPlayingIdProgress = playItem.defaultProgress
        }
        log("ceasedFile fileInfo :$fileInfo")
        return fileInfo
    }

    private fun parserPlayMediaItemInfo(mediaItem: MediaPlayItem?): MediaFileInfo? {
        val playItemInfo = mediaItemParser.parserPlayItemInfo(mediaItem)
        return parserPlayMediaItemInfo(playItemInfo)
    }

    private fun parserPlayMediaItemInfo(mediaPlayItemInfo: MediaPlayItemInfo?): MediaFileInfo? {
        val support = MediaFileUtils.supportFileType(mediaPlayItemInfo?.mediaClarity?.mediaUrl)
        log("MediaFileInfo support: $support")
        val fileInfo = MediaFileInfo(
            mediaItemParser.playingItem?.mediaId,
            mediaItemParser.playingItem?.mediaName,

            mediaItemParser.playingItemInfo?.mediaClarity?.mediaUrl,
            mediaItemParser.playingItemInfo?.mediaClarity?.mediaHeaders,

            mediaItemParser.playingItemInfo?.mediaClarity?.text,
            mediaItemParser.playingItemInfo?.mediaRate?.rate?.toString(),
            mediaItemParser.playingItemInfo?.mediaText?.path
        )
        log("MediaFileInfo fileInfo: $fileInfo")
        return if (!support) null else fileInfo
    }

    private fun log(message: String) {
        MediaLogUtil.log("intent parser: $message")
    }

    fun onDestroy() {
    }

    val playingItem get() = mediaItemParser.playingItem
    val playingItemInfo get() = mediaItemParser.playingItemInfo
    var singleChooseCallback = object : SingleChooseTypeCallback {

        override fun onCreateItem(type: SingleChooseType): ArrayList<out MediaCheckedData>? {
            return onCreateItemData(type)
        }

        override fun onChooseItem(
            type: SingleChooseType,
            index: Int,
            mediaCheckedData: MediaCheckedData,
            play: Boolean
        ) {
            onChooseItemPlay(type, index, mediaCheckedData, play)
        }
    }

    fun onCreateItemData(type: SingleChooseType): ArrayList<out MediaCheckedData>? {
        return when (type) {
            SingleChooseType.NONE -> {
                arrayListOf()
            }
            SingleChooseType.CLARITY -> {
                mediaItemParser.playingItem?.clarityArray
            }
            SingleChooseType.RATES -> {
                mediaItemParser.playingItem?.rateArray
            }
            SingleChooseType.TEXT -> {
                mediaItemParser.playingItem?.textArray
            }
        }
    }

    fun onChooseItemPlay(
        type: SingleChooseType,
        index: Int,
        mediaCheckedData: MediaCheckedData,
        play: Boolean
    ) {
        if (mediaCheckedData is MediaClarity) {
            mediaItemParser.changeSelectedClarity(mediaCheckedData)
        }
        if (mediaCheckedData is MediaRate) {
            mediaItemParser.changeSelectedRate(mediaCheckedData)
        }
        if (mediaCheckedData is MediaText) {
            mediaItemParser.changeSelectedText(mediaCheckedData)
        }
        lastPlayingIdProgress = intentHelperCall?.getVideoCurrentTime()
        reloadPlayer(play, 0)
    }
}