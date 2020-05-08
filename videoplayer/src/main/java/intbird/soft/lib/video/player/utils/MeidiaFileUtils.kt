package intbird.soft.lib.video.player.utils

import java.io.File
/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
object MeidiaFileUtils {
    fun getFileName(filePath: String?): String? {
        if (null == filePath || filePath.isEmpty()) {
            return null
        }
        val file = File(filePath)
        return if (file.exists()) file.name else null
    }
}