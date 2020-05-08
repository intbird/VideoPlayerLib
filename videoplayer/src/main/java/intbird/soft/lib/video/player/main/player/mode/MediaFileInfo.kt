package intbird.soft.lib.video.player.main.player.mode

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
data class MediaFileInfo(
        var filePath: String = "",
        var fileName: String? = "",
        var width: Int = 0,
        var height: Int = 0
) {
    var videoWHRate = 0f
        get() = width.toFloat() / height.toFloat()
}