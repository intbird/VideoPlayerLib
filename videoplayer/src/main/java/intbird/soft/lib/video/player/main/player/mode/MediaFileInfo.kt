package intbird.soft.lib.video.player.main.player.mode

/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
class MediaFileInfo(
    var mediaId: String = "",
    var mediaName: String? = "",
    var mediaUrl: String? = "",
    val mediaHeaders: Map<String, String>? = null,

    var clarity: String? = "",
    var rate: Float? = 0f,

    var width: Int = 0,
    var height: Int = 0
) {
    override fun toString(): String {
        return "MediaFileInfo(mediaId='$mediaId', mediaName=$mediaName, mediaPath='$mediaUrl', mediaHeaders=$mediaHeaders, clarity=$clarity, rate=$rate, width=$width, height=$height)"
    }
}