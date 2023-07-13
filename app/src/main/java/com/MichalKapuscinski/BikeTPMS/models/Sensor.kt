package com.MichalKapuscinski.BikeTPMS.models

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.MichalKapuscinski.BikeTPMS.MainActivity
import com.MichalKapuscinski.BikeTPMS.R
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.MonitorNotifier
import java.nio.ByteBuffer
import java.nio.ByteOrder
import androidx.lifecycle.Observer


class Sensor (_id: Int, _pressureBar: Int = 0, _temperatureC: Int = 0, _battery: Byte = 0, _leakAlert: Boolean = false) {

    private val PRESSURE_POS = 17
    private val TEMP_POS = 21
    private val BAT_POS = 25
    private val LEAK_FLAG_POS = 26

    var id: Int = _id
        private set(value) {
            field = value
        }
    var pressureBar: Int = _pressureBar
        private set(value) {
            field = value
        }
    var temperatureC: Int = _temperatureC
        private set(value) {
            field = value
        }
    var battery: Byte = _battery
        private set(value) {
            field = value
        }
    var leakAlert: Boolean = _leakAlert
        get() = field
        private set(value) {
            field = value
        }

    public fun updateMeasurementFromAdvData(advData: List<Long>) {
        pressureBar = decodeInt(advData[0].toInt())
        temperatureC = decodeInt(advData[1].toInt())
        battery = advData[2].toByte()
        leakAlert = when (advData[3]) {0.toLong() -> false; else -> true}
        Log.d("Moje", "pressure:" + pressureBar.toString())
        Log.d("Moje", "temp:" +temperatureC.toString())
        Log.d("Moje", "bat:" +battery.toString())

    }

    private fun decodeInt(codedValue: Int) : Int {
        val buffer = ByteBuffer.allocate(4)
        buffer.putInt(codedValue)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.flip()
        return buffer.int
    }


//    private fun sendNotification() {
//        val builder = NotificationCompat.Builder(this, "beacon-ref-notification-id")
//            .setSmallIcon(R.drawable.ic_stat_bike)
//            .setContentTitle("Zimówka")
//            .setContentText("Rear: " + "1,34" + "\t" + "Front: " + "2,45")
//        //.setPriority(NotificationCompat.PRIORITY_HIGH)
//        val stackBuilder = TaskStackBuilder.create(this)
//        stackBuilder.addNextIntent(Intent(this, MainActivity::class.java))
//        val resultPendingIntent = stackBuilder.getPendingIntent(
//            0,
//            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
//        )
//        builder.setContentIntent(resultPendingIntent)
//
//        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(notificationId, builder.build())
//        notificationCreated = true
//        //with(NotificationManagerCompat.from(context)) {
//        //    notify(notificationId, builder.build())
//        //}
//    }

}