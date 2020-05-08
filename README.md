LibVideoPlayer
========

This repo is for VideoPlayer

my blog: [https://blog.csdn.net/intbird/article/details/105970536](https://blog.csdn.net/intbird/article/details/105970536)

[video(video-QQXuJ6Lb-1588949363365)(type-bilibili)(url-https://player.bilibili.com/player.html?aid=668088806)(image-https://ss.csdn.net/p?http://i1.hdslb.com/bfs/archive/d0a0aeaa81eec14703263861a6ad53610643492f.jpg)(title-测试视频)]


step
--------
#### 1.add maven url in root project `build.gradle` file
```
repositories {
    google()
    jcenter()
    maven { url "http://intbird.world:8081/nexus/content/repositories/public/" }
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
 var path = "$video path"
 ServicesLoader.load(IVideoPlayer::class.java)?.startActivity(this, arrayListOf(path,path,path), defaultStartIndex=0)
```

Release
--------
see [change log](CHANGELOG.md) has release history.

$lastVersion is v1.0.0

```
dependencies {
    implementation 'intbird.soft.lib:video-player:1.0.0'
    implementation 'intbird.soft.lib:video-player-api:1.0.0'
}
```