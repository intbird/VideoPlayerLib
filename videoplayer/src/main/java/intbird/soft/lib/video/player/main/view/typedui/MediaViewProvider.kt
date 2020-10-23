package intbird.soft.lib.video.player.main.view.typedui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import intbird.soft.lib.video.player.R

/**
 * created by intbird
 * on 2020/9/10
 * DingTalk id: intbird
 */

data class MediaViewInfo<Display, Control>(
    val display: Display?,
    val control: Control?
)

class MediaViewProvider(private val view: View?) {

    fun by(mediaType: MediaPlayerType?): MediaViewInfo<out View, out View> {
        val mediaPlayerType = mediaType?: MediaPlayerType.PLAYER_STYLE_1
        addViewParent(view, mediaPlayerType)
        return MediaViewInfo(view?.findViewById(mediaPlayerType.viewDisplay), view?.findViewById(mediaPlayerType.viewControl))
    }

    private fun addViewParent(rootView: View?, viewStyle: MediaPlayerType) {
        val context = rootView?.context ?: return
        val displayLayout = rootView.findViewById<ViewGroup>(R.id.displayPlaceholder)
        displayLayout?.removeAllViews()
        LayoutInflater.from(context).inflate(viewStyle.layoutDisplay, displayLayout, true)

        val controlLayout = rootView.findViewById<ViewGroup>(R.id.controlPlaceholder)
        controlLayout?.removeAllViews()
        LayoutInflater.from(context).inflate(viewStyle.layoutControl, controlLayout, true)
    }
}
