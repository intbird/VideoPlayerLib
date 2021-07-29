package net.intbird.soft.lib.video.player.main.player.display.subtitle

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.TextUtils
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.CaptionStyleCompat
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.util.MimeTypes
import net.intbird.soft.lib.video.player.api.const.MediaTextConfig
import net.intbird.soft.lib.video.player.utils.MediaLogUtil

/**
 * created by intbird
 * on 2020/8/20
 * DingTalk id: intbird
 *
 *
 * i find this repo: https://github.com/averyzhong/SubtitleForAndroid
 */
class ExoPlayerSubtitle(val context: Context, val subtitleView: SubtitleView?) : ISubtitle {

    private var exoplayer: ExoPlayer? = null

    fun attachMediaPlayer() {
    }

    private fun addSubtitlePath(timedTextPath: String?) {
        log("addSubtitlePath:$timedTextPath")
        if (TextUtils.isEmpty(timedTextPath)
            || TextUtils.equals(timedTextPath, MediaTextConfig.SHOW_ICON)
            || TextUtils.equals(timedTextPath, MediaTextConfig.HIDE_ICON)) {
            return
        }
    }

    override fun onReceiveSubtitle(path: String?) {
        addSubtitlePath(path)
    }

    override fun onSubtitleChange(subtitle: String?) {
        log("onSubtitleChange-without-disappear-text:$subtitle")
    }

     fun setSubTitleView() {
        //playerView?.subtitleView?.setStyle(CaptionStyleCompat.createFromCaptionStyle())
        subtitleView?.setFixedTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 16f)
        subtitleView?.setStyle(
            CaptionStyleCompat(
                Color.WHITE, Color.TRANSPARENT, Color.parseColor("#70000000"),
                CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                Color.TRANSPARENT,
                Typeface.DEFAULT
            )
        )
    }

     fun setSubTitlePath(subtitlePath: String?):ArrayList<MediaItem.Subtitle> {
        val subtitleItem =  when {
            subtitlePath?.endsWith(".vtt") == true -> {
                MediaItem.Subtitle(Uri.parse(subtitlePath), MimeTypes.TEXT_VTT, null, C.SELECTION_FLAG_FORCED)
            }
            subtitlePath?.endsWith(".ssa") == true -> {
                MediaItem.Subtitle(Uri.parse(subtitlePath), MimeTypes.TEXT_SSA, null, C.SELECTION_FLAG_FORCED)
            }
            subtitlePath?.endsWith(".srt") == true -> {
                MediaItem.Subtitle(Uri.parse(subtitlePath), MimeTypes.APPLICATION_SUBRIP, null, C.SELECTION_FLAG_FORCED)
            }
            else -> {
                MediaItem.Subtitle(Uri.parse(subtitlePath), MimeTypes.APPLICATION_SUBRIP, null, C.SELECTION_FLAG_FORCED)
            }
        }
        return arrayListOf(subtitleItem)
    }

    private fun log(message: String) {
        MediaLogUtil.log("subtitle: $message")
    }
}