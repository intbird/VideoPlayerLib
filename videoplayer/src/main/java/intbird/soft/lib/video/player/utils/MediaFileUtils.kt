package intbird.soft.lib.video.player.utils

import intbird.soft.lib.video.player.main.player.mode.MediaFileType
import java.io.File
/**
 * created by intbird
 * on 2020/5/1
 * DingTalk id: intbird
 */
object MediaFileUtils {

    fun supportFileType(filePath: String?): Boolean {
        if (null == filePath || filePath.isEmpty()) {
            return false
        }
        return (filePath.startsWith(MediaFileType.FILE.type))
                || filePath.startsWith(MediaFileType.HTTP.type)
                || filePath.startsWith(MediaFileType.HTTPS.type)
    }

    private fun isFileType(filePath: String?, fileType: String?): Boolean {
        return filePath?.startsWith(fileType ?: "") ?: false
    }

    private fun existFile(filePath: String?): Boolean {
        return File(filePath).exists()
    }

    fun getFileName(filePath: String?): String? {
        if (isFileType(filePath, MediaFileType.FILE.type)) {
            val file = File(filePath)
            return if (file.exists()) file.name else null
        }
        return null
    }
}