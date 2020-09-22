package intbird.soft.app

import android.content.Context
import android.widget.TextView
import androidx.fragment.app.Fragment
import intbird.soft.lib.video.player.api.bean.MediaClarity
import intbird.soft.lib.video.player.api.bean.MediaPlayItem
import intbird.soft.lib.video.player.api.bean.MediaRate
import intbird.soft.lib.video.player.api.bean.MediaText
import intbird.soft.lib.video.player.api.const.MediaTextConfig
import intbird.soft.lib.video.player.api.state.IVideoPlayerCallback
import intbird.soft.lib.video.player.main.VideoPlayerFragment
import java.util.concurrent.TimeUnit

object MainActivityData {

    val itemTestIndex = 0

    val itemTestUrl1 = "https://intbird.s3.ap-northeast-2.amazonaws.com/Instagram.mp4"
    val itemTestUrl2 = "file:///sdcard/videos/My_Feed_on_Vimeo_0323_14_40_13.mp4"
    val itemTestUrl3 = "file:///sdcard/videos/tiktok_0409_10_55_07.mp4"
    val itemTestUrl4 =
        "https://intbird.s3.ap-northeast-2.amazonaws.com/476426784_mp4_h264_aac_hq.m3u8"
    val itemTestUrl5 = "https://llvod.mxplay.com/video/d89b306af415d293a66a74a26c560ab5/2/hls/h264_baseline.m3u8"

    val itemTestSrt1 =  "file:///sdcard/videos/srt1.srt"
    val itemTestSrt2 =  "https://sub.vanlong.stream/subdata/Undekhi.Season.1.Complete.srt"

    var itemTest1 = MediaPlayItem(
        "1", "fileName1",
        arrayListOf(
            MediaClarity("360P", itemTestUrl1),
            MediaClarity("720P", itemTestUrl1).checked(),
            MediaClarity("1080P", itemTestUrl1),
            MediaClarity("2K", itemTestUrl1),
            MediaClarity("4K", itemTestUrl1)
        ),
        arrayListOf(
            MediaRate("0.5",0.5f),
            MediaRate("normal",1.0f).checked(),
            MediaRate("1.5",1.5f),
            MediaRate("2",2.0f)
        ),
        arrayListOf(
            MediaText("关闭(显示关闭)", MediaTextConfig.SHOW_ICON),
            MediaText("中文", itemTestSrt1).checked(),
            MediaText("英文", itemTestSrt2)
        ),
        TimeUnit.SECONDS.toMillis(1)
    )
    var itemTest2 = MediaPlayItem(
        "2", "fileName2",
        arrayListOf(
            MediaClarity("360P", itemTestUrl2),
            MediaClarity("720P", itemTestUrl2).checked(),
            MediaClarity("1080P", itemTestUrl2),
            MediaClarity("2K", itemTestUrl2),
            MediaClarity("4K", itemTestUrl2)
        ), arrayListOf(
            MediaRate("0.5",0.5f).checked(),
            MediaRate("normal",1.0f),
            MediaRate("1.5",1.5f),
            MediaRate("2",2.0f)
        ), arrayListOf(
            MediaText("关闭(隐藏关闭)", MediaTextConfig.HIDE_ICON),
            MediaText("中文", itemTestSrt1).checked(),
            MediaText("英文", itemTestSrt2)
        ), TimeUnit.SECONDS.toMillis(10)
    )
    var itemTest3 = MediaPlayItem(
        "3", "fileName3",
        arrayListOf(
            MediaClarity("360P", itemTestUrl3, mapOf("Key" to "value")),
            MediaClarity("720P", itemTestUrl3),
            MediaClarity("1080P", itemTestUrl3),
            MediaClarity("2K", itemTestUrl3),
            MediaClarity("4K", itemTestUrl3)
        ), arrayListOf(
            MediaRate("0.5",0.5f).checked(),
            MediaRate("normal",1.0f),
            MediaRate("1.5",1.5f),
            MediaRate("2",2.0f)
        ), arrayListOf(
            MediaText("关闭").checked(),
            MediaText("中文", itemTestSrt1),
            MediaText("英文", itemTestSrt1)
        ), TimeUnit.SECONDS.toMillis(30)
    )
    var itemTest4 = MediaPlayItem(
        "4", "fileName4",
        arrayListOf(
            MediaClarity("360P", itemTestUrl4),
            MediaClarity("720P", itemTestUrl4),
            MediaClarity("1080P", itemTestUrl4),
            MediaClarity("2K", itemTestUrl1),
            MediaClarity("4K", itemTestUrl2)
        ), arrayListOf(
            MediaRate("0.5",0.5f).checked(),
            MediaRate("normal",1.0f),
            MediaRate("1.5",1.5f),
            MediaRate("2",2.0f)
        ), arrayListOf(
            MediaText("关闭").checked(),
            MediaText("中文", itemTestSrt1),
            MediaText("英文", itemTestSrt1)
        ), TimeUnit.SECONDS.toMillis(5)
    )

    var itemTest5 = MediaPlayItem(
        "5", "fileName5",
        arrayListOf(
            MediaClarity("360P", itemTestUrl5),
            MediaClarity("720P", itemTestUrl5),
            MediaClarity("1080P", itemTestUrl5),
            MediaClarity("2K", itemTestUrl5),
            MediaClarity("4K", itemTestUrl5)
        ), arrayListOf(
            MediaRate("0.5",0.5f).checked(),
            MediaRate("normal",1.0f),
            MediaRate("1.5",1.5f),
            MediaRate("2",2.0f)
        ), arrayListOf(
            MediaText("关闭").checked(),
            MediaText("中文", itemTestSrt1),
            MediaText("英文", itemTestSrt1)
        ), TimeUnit.SECONDS.toMillis(5)
    )


    val itemTestArrayString = arrayOf(itemTestUrl1, itemTestUrl2, itemTestUrl3, itemTestUrl4, itemTestUrl5)
    val itemTestArrayModel = arrayListOf(itemTest1, itemTest2, itemTest3, itemTest4, itemTest5)

    val itemTestArray1 = arrayListOf(itemTest1)
    val itemTestArray2 = arrayListOf(itemTest1, itemTest2, itemTest3, itemTest4, itemTest5)
    val itemTestArray3 = arrayListOf(itemTest1, itemTest2, itemTest3, itemTest4, itemTest5)


    class ReceivePlayerCallback(val context: Context, val stateText: TextView, val videoPlayerFragment: VideoPlayerFragment) :
        IVideoPlayerCallback {
        override fun onCreated(fragment: Fragment) {
        }

        override fun onPrepare() {
            stateText.text = "MainActivity: onPrepare loading... \n\n ${videoPlayerFragment?.getVideoPlayerStateInfo()?.getVideoPlayingItemInfo()}"
        }

        override fun onPrepared() {
            stateText.text = "MainActivity: onPrepared"
        }

        override fun onStart() {
            stateText.text = "MainActivity: onStart \n\n ${videoPlayerFragment?.getVideoPlayerStateInfo()?.getVideoPlayingItemInfo()}"
        }

        override fun onSeekTo(progress: Long?) {
            stateText.text = "MainActivity: onSeekTo:$progress"
        }

        override fun onPause(progress: Long?) {
            stateText.text = "MainActivity: onPause: ${videoPlayerFragment?.getVideoPlayerStateInfo()?.getCurrentTime()}"
        }

        override fun onCompletion() {
            stateText.text = "MainActivity: onCompletion"
        }

        override fun onStop() {
            stateText.text = "MainActivity: onStop"
        }

        override fun onError(errorCode: Int, errorMessage: String?) {
            stateText.text = "MainActivity: onError: $errorCode  = $errorMessage"
        }

        override fun onBuffStart() {
            stateText.text = "MainActivity: onBuffStart"
        }

        override fun onBuffEnded() {
            stateText.text = "MainActivity: onBuffEnded"
        }
    }
}
