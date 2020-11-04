package net.intbird.soft.lib.video.player.main.player.display.surface

import android.graphics.SurfaceTexture
import android.view.TextureView

/**
 * created by intbird
 * on 2020/8/20
 * DingTalk id: intbird
 */
class TextureDisplay(private val iDisplay: IDisplay) : TextureView.SurfaceTextureListener {
    override fun onSurfaceTextureSizeChanged(
        surface: SurfaceTexture?,
        width: Int,
        height: Int
    ) {
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        iDisplay.displayStateChange(true)
    }
}