package intbird.soft.lib.video.player.main.user

import android.content.Context
import android.media.AudioManager
import intbird.soft.lib.video.player.main.player.IPlayer

/**
 * created by intbird
 * on 2020/09/28
 * DingTalk id: intbird
 *
 * https://developer.android.com/guide/topics/media-apps/audio-focus
 */
class AudioFocusChangeManager(val context: Context, val player: IPlayer?) {
    private var mAudioManager: AudioManager? = null

    fun requestAudioFocus(): Boolean {
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val result: Int? = mAudioManager?.requestAudioFocus(
            audioFocus,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun abandonAudioFocus() {
        mAudioManager?.abandonAudioFocus(audioFocus)
    }

    private val audioFocus = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (player?.isPlaying() == false) player?.start()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (player?.isPlaying() == true) player?.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (player?.isPlaying() == true) player?.pause()
            }
        }
    }
}