package intbird.soft.lib.video.player.main.player.display.subtitle

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.TimedText
import android.net.Uri
import android.text.TextUtils
import android.widget.TextView
import intbird.soft.lib.video.player.api.const.MediaTextConfig
import intbird.soft.lib.video.player.utils.MediaLogUtil

/**
 * created by intbird
 * on 2020/8/20
 * DingTalk id: intbird
 *
 *
 * i find this repo: https://github.com/averyzhong/SubtitleForAndroid
 */
class MediaPlayerSubtitle(val context: Context, val subtitleView: TextView) : ISubtitle {

    private var mediaPlayer: MediaPlayer? = null

    fun attachMediaPlayer(mediaPlayer: MediaPlayer?) {
        this.mediaPlayer = mediaPlayer
    }

    private fun addSubtitlePath(timedTextPath: String?) {
        log("addSubtitlePath:$timedTextPath")
        if (TextUtils.isEmpty(timedTextPath)
            || TextUtils.equals(timedTextPath, MediaTextConfig.SHOW_ICON)
            || TextUtils.equals(timedTextPath, MediaTextConfig.HIDE_ICON)) {
            return
        }
        try {
            log("mediaPlayer:$mediaPlayer")
            mediaPlayer?.addTimedTextSource(
                context,
                Uri.parse(timedTextPath),
                MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP
            )
            val textTrackIndex = findTrackIndexFor(
                MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT,
                mediaPlayer?.trackInfo
            )
            if (textTrackIndex >= 0) {
                mediaPlayer?.selectTrack(textTrackIndex)
            }
            log("textTrackIndex:$textTrackIndex")

            mediaPlayer?.setOnTimedTextListener { _, text ->
                log("textTrackIndex:$textTrackIndex")
                onSubtitleChange(text.text)
            }

            mediaPlayer?.setOnSeekCompleteListener { }

            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        } catch (e: Exception) {
        }
    }

    private fun findTrackIndexFor(
        mediaTrackType: Int,
        trackInfo: Array<MediaPlayer.TrackInfo>?
    ): Int {
        if (null == trackInfo) return -1
        val index = -1
        for (i in trackInfo.indices) {
            if (trackInfo[i].trackType == mediaTrackType) {
                return i
            }
        }
        return index
    }

    override fun onReceiveSubtitle(path: String?) {
        addSubtitlePath(path)
    }

    override fun onSubtitleChange(subtitle: String?) {
        log("onSubtitleChange-without-disappear-text:$subtitle")
        subtitleView.text = subtitle?:""
    }

    private fun log(message: String) {
        MediaLogUtil.log("subtitle: $message")
    }
}