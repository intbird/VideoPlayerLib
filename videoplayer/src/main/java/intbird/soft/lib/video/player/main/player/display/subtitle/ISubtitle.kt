package intbird.soft.lib.video.player.main.player.display.subtitle

interface ISubtitle {
    fun onReceiveSubtitle(path: String?)

    fun onSubtitleChange(subtitle: String?)
}