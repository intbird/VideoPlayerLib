package net.intbird.soft.lib.video.player.main.player.player;

import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import net.intbird.soft.lib.video.player.main.player.IPlayer
import net.intbird.soft.lib.video.player.main.player.call.IPlayerCallback
import net.intbird.soft.lib.video.player.main.player.mode.MediaFileInfo
import net.intbird.soft.lib.video.player.utils.MediaLogUtil

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 *
 * viewImpl: 暂时没时间view接口,直接用view实现
 */
class WebViewPlayerImpl(
    private val context: Context,
    private val webView: WebView?,
    private val playerCallback: IPlayerCallback?
) : IPlayer {

    init {
        //webView?.settings?.setSupportZoom(true)
        //webView?.settings?.javaScriptEnabled = true
        webView?.webChromeClient = object : WebChromeClient() {
        }
        webView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return true
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                playerCallback?.onBuffStart()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                playerCallback?.onBuffEnded()
            }
        }
    }

    override fun onParamsChange(mediaFileInfo: MediaFileInfo?) {
        if (null == mediaFileInfo) return
    }

    override fun prepare(mediaFileInfo: MediaFileInfo) {
        webView?.loadUrl("https://intbird.net")
    }

    override fun start() {
    }

    override fun seekTo(duration: Long, autoPlay: Boolean) {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun last(): Boolean {
        return false
    }

    override fun next(): Boolean {
        return false
    }

    override fun stop() {
    }

    override fun destroy() {
        webView?.removeAllViews()
        webView?.destroy()
    }

    override fun isPlaying(): Boolean {
        return false
    }

    override fun getCurrentTime(): Long {
        return 0
    }

    override fun getTotalTime(): Long {
        return 0
    }

    private fun log(message: String) {
        MediaLogUtil.log("WebPlayerImpl: $message")
    }
}