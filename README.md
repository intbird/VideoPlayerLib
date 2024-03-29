LibVideoPlayer
========

This repo is for VideoPlayer

my website: [https://intbird.net](https://intbird.net)  

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

##### auto rotation
![image](https://camo.githubusercontent.com/efdb70638a6f376337375820bde48de3d7fa10180ea74cf9d0f9e5e5cfc17dab/68747470733a2f2f696d672d626c6f672e6373646e696d672e636e2f32303230313031343137303934383332362e676966237069635f63656e746572)


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
     implementation 'net.intbird.soft.lib:video-player:$lastVersion'
     implementation 'net.intbird.soft.lib:video-player-api:$lastVersion'
}
```


#### 3.add method in your code where you need to play video.

1. support
```
        net.intbird.soft.lib.video.player.main.player.player.ExoPlayerImpl

        net.intbird.soft.lib.video.player.main.player.player.MediaPlayerImpl

        net.intbird.soft.lib.video.player.main.player.player.WebViewPlayerImpl

```

2. useage
```
        
        val itemTestUrl1 = "file:///sdcard/videos/Instagram_0312_10_19_20.mp4"
        val itemTestUrl2 = "https://intbird.s3.ap-northeast-2.amazonaws.com/h264_baseline.m3u8"


         // use as a fragment
        add1.setOnClickListener { 
            addVideoPlayer(R.id.fragment_player, MediaPlayerType.PLAYER_STYLE_1) 
        }
        
        add2.setOnClickListener { 
            addVideoPlayer(R.id.fragment_player, MediaPlayerType.PLAYER_STYLE_2)
        }

        add3.setOnClickListener { 
            addVideoPlayer(R.id.fragment_player, MediaPlayerType.PLAYER_STYLE_3)
        }
        remove.setOnClickListener { removeAudioPlayer(R.id.fragment_player) }


       

        // full screen activity
        fullScreen1.setOnClickListener {
            ServicesLoader.load(IVideoPlayer::class.java)?.startActivity(this, itemTestArrayModel, itemTestIndex, autoPlay = true)
        }

        // full screen activity
        fullScreen2.setOnClickListener {
            ServicesLoader.load(IVideoPlayer::class.java)?.startActivity(this, itemTestArrayString, itemTestIndex,autoPlay = true)
        }
```

3. control
```
        reset.setOnClickListener {
            videoPlayerFragment?.setVideoPlayerList(itemTestArray3, itemTestIndex,true)
        }
        last.setOnClickListener {
             videoPlayerFragment?.getVideoPlayerController()?.last() 
        }
        pause.setOnClickListener { 
            videoPlayerFragment?.getVideoPlayerController()?.pause()
        }
        next.setOnClickListener { 
            videoPlayerFragment?.getVideoPlayerController()?.next() 
        }
        info.setOnClickListener { 
            stateText.text = "info:${ videoPlayerFragment?.getVideoPlayerStateInfo()?.getVideoPlayingItemInfo()}" }

```


Release
--------
see [change log](CHANGELOG.md) has release history.

newest $versionName is v1.1.2-SNAPSHOT

```
dependencies {
   implementation "net.intbird.soft.lib:video-player:$versionName"
   implementation "net.intbird.soft.lib:video-player-api:$versionName"
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

###### issues
> https://github.com/intbird/VideoPlayerLib/issues/2  


##### recoding...

###### VideoPlyaerView: windowmanager,viewPager2,RecyclerView 内部需要引入的View
> 修复: 权限申请要在activity或者fragment中,view要独立,切换数据和页面等等,要做成一个服务


###### windowmanager: 开启和关闭播放器小窗口: 用系统广播进行传递, 效率太低和surface圆角裁剪, 需要改进!   
> 问题: 小窗和大窗每次需要对资源进行重载.    
> 修复: 需要改进为可重复使用,挂载到不同的surfaceview上.      
> ![image](https://user-images.githubusercontent.com/7553539/127451044-5e30b23f-b5e7-4411-bfc2-af259e974716.png)  

 
###### viewPager2:大家一起仿抖音(滑动youtube的webview实现的播放器接口 和 流媒体视频)  
> ![image](https://user-images.githubusercontent.com/7553539/127451347-123cd39c-2a1e-4fa7-92eb-92534bb6a062.png)

 
###### style5:弹出菜单等不再视频内部,弹层选择播放器速率和字幕,以及播放器的各种样式UI等  
> 问题: 不管是在视频内部弹出视频还是使用独立的对话框弹出,都需要播放器内部定义样式    
> 修复: 公开内部api可实现的功能,外部获取当前参数和信息,弹出自定义UI(xml文件或者api)后,通过接口设置播放  
> style5:   
> ![image](https://user-images.githubusercontent.com/7553539/127448087-9cb6a895-a54b-408d-ba6d-bbad94cd9a6a.png)  
> popwindow:   
> ![image](https://user-images.githubusercontent.com/7553539/127448132-6249b8ec-a301-4edc-8279-4bf5c0094cbc.png)  

###### etc...
