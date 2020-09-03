package intbird.soft.lib.video.player.main.player.mode

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
class MediaFileInfo(
    var mediaId: String = "",
    var mediaName: String? = "",
    var mediaPath: String = "",
    val mediaHeaders: Map<String, String>? = null,

    var clarity: String? = "",
    var width: Int = 0,
    var height: Int = 0
) {

    override fun toString(): String {
        return "MediaFileInfo(mediaId='$mediaId', mediaName=$mediaName, mediaPath='$mediaPath', mediaHeaders=$mediaHeaders, clarity=$clarity, width=$width, height=$height)"
    }
}