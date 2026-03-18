package com.sanomush.tari.helper

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.roundToInt

class CompassHelper(private val context: Context) : SensorEventListener {

    interface CompassListener {
        fun onAzimuthChanged(azimuth: Float, direction: String, degrees: Int)
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    private var listener: CompassListener? = null
    private var currentAzimuth: Float = 0f
    private val alpha = 0.1f

    fun start(listener: CompassListener) {
        this.listener = listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        listener = null
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                gravity[0] = alpha * event.values[0] + (1 - alpha) * gravity[0]
                gravity[1] = alpha * event.values[1] + (1 - alpha) * gravity[1]
                gravity[2] = alpha * event.values[2] + (1 - alpha) * gravity[2]
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                geomagnetic[0] = alpha * event.values[0] + (1 - alpha) * geomagnetic[0]
                geomagnetic[1] = alpha * event.values[1] + (1 - alpha) * geomagnetic[1]
                geomagnetic[2] = alpha * event.values[2] + (1 - alpha) * geomagnetic[2]
            }
        }

        val success = SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)
        if (success) {
            SensorManager.getOrientation(rotationMatrix, orientation)
            var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            azimuth = (azimuth + 360) % 360
            currentAzimuth = smoothAngle(currentAzimuth, azimuth)

            val direction = getCardinalDirection(currentAzimuth)
            val degrees = currentAzimuth.roundToInt()
            listener?.onAzimuthChanged(currentAzimuth, direction, degrees)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun smoothAngle(current: Float, target: Float): Float {
        var diff = target - current
        if (diff > 180) diff -= 360
        if (diff < -180) diff += 360
        return (current + diff * 0.15f + 360) % 360
    }

    private fun getCardinalDirection(azimuth: Float): String {
        return when {
            azimuth < 22.5 || azimuth >= 337.5 -> "Utara (N)"
            azimuth < 67.5 -> "Timur Laut (NE)"
            azimuth < 112.5 -> "Timur (E)"
            azimuth < 157.5 -> "Tenggara (SE)"
            azimuth < 202.5 -> "Selatan (S)"
            azimuth < 247.5 -> "Barat Daya (SW)"
            azimuth < 292.5 -> "Barat (W)"
            azimuth < 337.5 -> "Barat Laut (NW)"
            else -> "Utara (N)"
        }
    }

    fun isAvailable(): Boolean {
        return magnetometer != null && accelerometer != null
    }
}