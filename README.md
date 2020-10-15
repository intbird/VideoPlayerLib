LibVideoPlayer
========

This repo is for VideoPlayer

my blog: [https://blog.csdn.net/intbird/article/details/105970536](https://blog.csdn.net/intbird/article/details/105970536)

# Overview  
### fragment
#### play, pause, last, next... & resolution, subtitle, speed...
![image](screenshoots/00.png)

![image](screenshoots/0.png)

### touch
#### progress, volume, brightness, doubleTap...
![image](screenshoots/01.png)

### landscape
##### auto rotate screen 
![image](screenshoots/02.png)

How to Use it
--------
#### 1.add maven url in root project `build.gradle` file
```
repositories {
    google()
    jcenter()
    maven { url "https://intbird.net/maven/releases/" }
```


#### 2.add dependence in app project `build.gradle` file
```
dependencies {
     implementation 'intbird.soft.lib:video-player:$lastVersion'
     implementation 'intbird.soft.lib:video-player-api:$lastVersion'
}
```


#### 3.add method in your code where you need to play video.
```
         // suppport file type
        enum class MediaFileType(val type: String) {
            FILE("file:///"),
            HTTP("http://"),
            HTTPS("https://")
        }
        
        val itemTestUrl1 = "file:///sdcard/videos/Instagram_0312_10_19_20.mp4"
        val itemTestUrl2 = "https://intbird.s3.ap-northeast-2.amazonaws.com/h264_baseline.m3u8"

         // use as a fragment
        add1.setOnClickListener { addVideoPlayer(R.id.fragment_player, MediaPlayerType.PLAYER_STYLE_1) }
        add2.setOnClickListener { addVideoPlayer(R.id.fragment_player, MediaPlayerType.PLAYER_STYLE_2) }
        remove.setOnClickListener { removeAudioPlayer(R.id.fragment_player) }

        reset.setOnClickListener {
            videoPlayerFragment?.setVideoPlayerList(itemTestArray3, itemTestIndex,true)
        }
        mdf1.setOnClickListener {
            val playingItem = videoPlayerFragment?.getVideoPlayerStateInfo()?.getVideoPlayingItem() ?: return@setOnClickListener
            playingItem.mediaName = "modify media title"
            videoPlayerFragment?.setVideoPlayerItem(playingItem)
        }
        mdf2.setOnClickListener {
            val playingItemInfo = videoPlayerFragment?.getVideoPlayerStateInfo()?.getVideoPlayingItemInfo() ?: return@setOnClickListener
            playingItemInfo.mediaRate = MediaRate(2.0f)
            videoPlayerFragment?.setVideoPlayerItemInfo(playingItemInfo)
        }

        last.setOnClickListener { videoPlayerFragment?.getVideoPlayerController()?.last() }
        pause.setOnClickListener { videoPlayerFragment?.getVideoPlayerController()?.pause() }
        next.setOnClickListener { videoPlayerFragment?.getVideoPlayerController()?.next() }
        info.setOnClickListener { stateText.text = "info:${videoPlayerFragment?.getVideoPlayerStateInfo()?.getVideoPlayingItemInfo()}" }

        // full screen activity
        fullScreen1.setOnClickListener {
            ServicesLoader.load(IVideoPlayer::class.java)?.startActivity(this, itemTestArrayModel, itemTestIndex, autoPlay = true)
        }

        // full screen activity
        fullScreen2.setOnClickListener {
            ServicesLoader.load(IVideoPlayer::class.java)?.startActivity(this, itemTestArrayString, itemTestIndex,autoPlay = true)
        }
```

Release
--------
see [change log](CHANGELOG.md) has release history.

newest $versionName is v1.1.2-SNAPSHOT

```
dependencies {
   implementation "intbird.soft.lib:video-player:$versionName"
   implementation "intbird.soft.lib:video-player-api:$versionName"
}
```

------

# ScreenShoots

## DemoActivity  
![image](screenshoots/demo.png)

## style1 portrait
![image](screenshoots/1.png)

## style1 landscape
![image](screenshoots/2.png)

## style2 portrait
![image](screenshoots/3.png)

## style2 landscape
![image](screenshoots/4.png)

##  slide progress
![image](screenshoots/6.png)

## slide light
![image](screenshoots/7.png)

## slide volume
![image](screenshoots/8.png)

##  choose clarity
![image](screenshoots/9.png)


##  locker
![image](screenshoots/10.png)

##  developing...