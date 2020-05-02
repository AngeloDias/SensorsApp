package br.com.training.android.sensorsapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var lightSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null
    private var sensorManager: SensorManager? = null
    private var isRunning = false
    private var xOld = 0.0
    private var yOld = 0.0
    private var zOld = 0.0
    private var threshold = 1500.0
    private var oldTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)
        accelerometerSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if(lightSensor == null ){
            Toast.makeText(applicationContext, "Light sensor isn't available", Toast.LENGTH_LONG).show()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val tempSensor = sensorEvent!!.sensor!!

        Log.d("sensorsDebug", "Sensor: ${tempSensor.type}")

        if (tempSensor.type == Sensor.TYPE_LIGHT) {

            if (sensorEvent.values[0] > 40 && !isRunning) {
                isRunning = true

                try {
                    val mediaPlayer = MediaPlayer()

                    mediaPlayer.setDataSource("https://ia801900.us.archive.org/7/items/100ClassicalMusicMasterpieces/1709%20Bach%20%2C%20Toccata%20in%20D%20minor.mp3")
                    mediaPlayer.prepare()
                    mediaPlayer.start()

                } catch (exc: Exception) {}
            }

        } else if(tempSensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = sensorEvent.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]
            val currentTime = System.currentTimeMillis()
            val timeDiff = currentTime - oldTime

            Log.d("sensorsDebug", "Inside accelerometer if")

            if(timeDiff > 100) {
                oldTime = currentTime
                val speed = abs(x + y + z - xOld - yOld - zOld) / timeDiff  * 10000

                Log.d("sensorsDebug", "Inside accelerometer if, after speed")

                if(speed > threshold) {
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                    Log.d("sensorsDebug", "Inside accelerometer if, after vibrator")

                    vibrator.vibrate(500)
                    Toast.makeText(applicationContext, "shock", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(this,  lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(this,  accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

}
