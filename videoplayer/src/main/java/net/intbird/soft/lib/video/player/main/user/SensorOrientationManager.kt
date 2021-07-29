package net.intbird.soft.lib.video.player.main.user

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.OrientationEventListener
import net.intbird.soft.lib.video.player.main.player.IPlayer
import net.intbird.soft.lib.video.player.utils.MediaLogUtil
import java.util.concurrent.TimeUnit

/**
 * created by intbird
 * on 2020/09/28
 * DingTalk id: intbird
 *
 * https://developer.android.com/guide/topics/sensors/sensors_overview
 *
 * OrientationEventListener: 用来跟随传感器做横竖屏切换 -> 开启条件(默认 + 传感器动作识别) & 关闭条件(用户锁定方向+手动点击切换等),
 * onConfigurationChanged: 屏幕方向切换结果监听-> 触发 关闭条件
 * SensorEventListener: 传感器动作识别-> 触发 开启条件(也可以变更传感器检测开启 为 当手机方向和视频方向一致时开启(效果不咋滴,先删掉了))
 */
class SensorOrientationManager(
    val activity: Activity,
    val player: IPlayer?
) {

    private val sensorTime = 3 // SensorManager.SENSOR_DELAY_NORMAL or time

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private var sensorEvent: SensorEventListener? = null

    private var orientationEventListener: OrientationEventListener? = null

    init {
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        // val gravitySensors = sensorManager?.getSensorList(Sensor.TYPE_GRAVITY)
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)
        if (sensor == null) {
            sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
        if (sensor != null) {
            //MediaLogUtil.log("Sensor sensor: success")
            sensorEvent = object : SensorEventListener {

                override fun onSensorChanged(event: SensorEvent?) {
                    //MediaLogUtil.log("Sensor onSensorChanged: ${(event?.values?.get(2) ?: 0)} ")
                    if (null == event) return
                    enableOrientationEvent(event.values[2] <= 5) // 0 ~ 10
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                }
            }

            // Sensor.TYPE_ORIENTATION 懒省事,直接用
            orientationEventListener = object : OrientationEventListener(
                activity, sensorTime
            ) {
                override fun onOrientationChanged(orientation: Int) {
                    MediaLogUtil.log("Sensor onOrientationChanged: $orientation ")
                    if (orientation == ORIENTATION_UNKNOWN) return
                    if (orientation > 350 || orientation < 10) {
                        activity.requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                    } else if (orientation in 81..99) {
                        activity.requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    } else if (orientation in 171..189) {
                        activity.requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                    } else if (orientation in 261..279) {
                        activity.requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    }
                }
            }
        }
    }

    fun requestSensor() {
        MediaLogUtil.log("Sensor requestSensor")
        sensorManager?.registerListener(sensorEvent, sensor, sensorTime)
        orientationEventListener?.enable()
    }

    fun abandonSensor() {
        MediaLogUtil.log("Sensor abandonSensor")
        sensorManager?.unregisterListener(sensorEvent)
        orientationEventListener?.disable()
    }

    fun enableOrientationEvent(enable: Boolean) {
        if (enable) {
            orientationEventListener?.enable()
        } else {
            orientationEventListener?.disable()
        }
    }

}