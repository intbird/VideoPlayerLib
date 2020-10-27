package net.intbird.soft.lib.video.player.api.bean

/**
 * created by intbird
 * on 2020/9/8
 * DingTalk id: intbird
 */
enum class MediaFileType(val type: String) {
    FILE("file:///"),
    HTTP("http://"),
    HTTPS("https://"),
    WEBSITE("website://")
}