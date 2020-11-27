package net.intbird.soft.lib.video.player.main.view.typedui

import net.intbird.soft.lib.video.player.R

/**
 * created by intbird
 * on 2020/9/10
 * DingTalk id: intbird
 */
enum class MediaPlayerType(
    open val layoutDisplay: Int,
    open val viewDisplay: Int,
    open val layoutControl: Int,
    open val viewControl: Int
) {
    PLAYER_STYLE_WM_1(
        R.layout.lib_media_player_diaplay_texture,
        R.id.textureView,
        R.layout.lib_media_player_control_style_wm_1,
        R.id.mediaRootControl
    ),
    PLAYER_STYLE_WM_2(
        R.layout.lib_media_player_diaplay_texture,
        R.id.textureView,
        R.layout.lib_media_player_control_style_wm_2,
        R.id.mediaRootControl
    ),
    PLAYER_STYLE_EXO(
        R.layout.lib_media_player_diaplay_exoview,
        R.id.exoplayerview,
        R.layout.lib_media_player_control_style_exo,
        R.id.mediaRootControl
    ),
    PLAYER_STYLE_WEBVIEW(
        R.layout.lib_media_player_diaplay_webview,
        R.id.webview,
        R.layout.lib_media_player_control_style_webview,
        R.id.mediaRootControl
    )
}