package net.intbird.soft.lib.video.player.main.intent

import android.os.Bundle
import net.intbird.soft.lib.video.player.api.bean.MediaCheckedData
import net.intbird.soft.lib.video.player.api.bean.MediaPlayItem
import net.intbird.soft.lib.video.player.api.bean.MediaPlayItemInfo
import net.intbird.soft.lib.video.player.main.VideoPlayerFragment
import net.intbird.soft.lib.video.player.main.view.dialog.type.SingleChooseType
import net.intbird.soft.lib.video.player.main.intent.delegate.PlayerListDelegate
import net.intbird.soft.lib.video.player.main.intent.parser.MediaItemInfoParer
import net.intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import net.intbird.soft.lib.video.player.main.view.typedui.MediaPlayerType
import net.intbird.soft.lib.video.player.utils.MediaFileUtils
import net.intbird.soft.lib.video.player.utils.MediaLogUtil

/**
 * created by intbird
 * on 2020/9/10
 * DingTalk id: intbird
 */
class MediaIntentHelper(
    private val arguments: Bundle?,
    private val intentHelperCall: MediaIntentHelperCall?
) : PlayerListDelegate {

    interface MediaIntentHelperCall {
        fun getVideoCurrentTime(): Long?
        fun onReceivePlayFile(reload: Boolean, mediaFileInfo: MediaFileInfo?)
    }

    private var videoPlayItems: ArrayList<MediaPlayItem>? = null
    private var videoPlayIndex: Int = 0
    private var videoAutoPlay: Boolean? = null

    val mediaPlayerType get() = arguments?.getSerializable(VideoPlayerFragment.EXTRA_PLAYER_TYPE) as? MediaPlayerType
    val isAutoStartPlay get() = (videoAutoPlay == true)

    private var mediaItemParser = MediaItemInfoParer()
    private var mediaItemRecord = MediaItemInfoRecord()

    fun startPlayVideoPlayer() {
        startPlayVideoPlayer(
            arguments?.getParcelableArrayList<MediaPlayItem>(VideoPlayerFragment.EXTRA_FILE_URLS),
            arguments?.getInt(VideoPlayerFragment.EXTRA_FILE_INDEX) ?: 0,
            arguments?.getBoolean(VideoPlayerFragment.EXTRA_PLAYER_AUTO_PLAY) ?: true
        )
    }

    fun startPlayVideoPlayer(
        playList: ArrayList<MediaPlayItem>?,
        playIndex: Int,
        autoPlay: Boolean
    ): MediaIntentHelper {
        videoPlayItems = playList
        videoPlayIndex = playIndex
        videoAutoPlay = autoPlay
        reloadPlayer(autoPlay, 0)
        log("startPlayVideoPlayer: playList:$playList playIndex: $playIndex autoPlay: $autoPlay")
        return this
    }

    fun setVideoPlayerItem(mediaPlayItem: MediaPlayItem?, autoPlay: Boolean) {
        reloadPlayer(autoPlay, mediaPlayItem)
    }

    fun setVideoPlayerItemInfo(mediaPlayItemInfo: MediaPlayItemInfo?, autoPlay: Boolean) {
        reloadPlayer(autoPlay, mediaPlayItemInfo)
    }

    private fun reloadPlayer(reload: Boolean, mediaPlayItem: MediaPlayItem?): Boolean {
        val fileInfo = parserPlayMediaItemInfo(mediaPlayItem)
        mediaItemRecord.save(
            mediaPlayItem?.mediaId,
            MediaRecordData(intentHelperCall?.getVideoCurrentTime())
        )
        intentHelperCall?.onReceivePlayFile(reload, fileInfo)
        return null != fileInfo
    }

    private fun reloadPlayer(reload: Boolean, mediaPlayItemInfo: MediaPlayItemInfo?): Boolean {
        val fileInfo = parserPlayMediaItemInfo(mediaPlayItemInfo)
        mediaItemRecord.save(
            mediaPlayItemInfo?.mediaId,
            MediaRecordData(intentHelperCall?.getVideoCurrentTime())
        )
        intentHelperCall?.onReceivePlayFile(reload, fileInfo)
        return null != fileInfo
    }

    private fun reloadPlayer(reload: Boolean, crease: Int): Boolean {
        val creasedFile = getCreasedFile(crease)
        return if (null != creasedFile) {
            videoPlayIndex = videoPlayIndex.plus(crease)
            mediaItemRecord.save(
                creasedFile.mediaId,
                MediaRecordData(intentHelperCall?.getVideoCurrentTime())
            )
            intentHelperCall?.onReceivePlayFile(reload, creasedFile)
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
            mediaItemParser.playingItemInfo?.mediaRate?.rate,
            mediaItemParser.playingItemInfo?.mediaText?.path
        )
        log("MediaFileInfo fileInfo: $fileInfo")
        return if (!support) null else fileInfo
    }

    fun onDestroy() {
    }

    private fun log(message: String) {
        MediaLogUtil.log("intent parser: $message")
    }


    fun getLastPlayingProgress(mediaId: String?): Long? {
        return mediaItemRecord.get(mediaId)?.lastProgress
    }

    val playingItem get() = mediaItemParser.playingItem
    val playingItemInfo get() = mediaItemParser.playingItemInfo


    override fun delegatePlay(): Boolean {
        return reloadPlayer(true, 0)
    }

    override fun delegateLast(): Boolean {
        return reloadPlayer(true, -1)
    }

    override fun delegateNext(): Boolean {
        return reloadPlayer(true, 1)
    }

    fun delegateCreateSingleChooseData(type: SingleChooseType): ArrayList<out MediaCheckedData>? {
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

    fun delegateSelectedSingleChooseData(
        type: SingleChooseType,
        index: Int,
        mediaCheckedData: MediaCheckedData
    ) {
        mediaItemParser.changeSelectedData(mediaCheckedData)

        if(type == SingleChooseType.CLARITY) {
            reloadPlayer(true, 0)
        } else {
            reloadPlayer(false, 0)
        }
    }
}