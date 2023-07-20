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
import androidx.room.Ignore


//class Sensor (_id: Int, _lowPressureTh: Int) {
//
////    private val PRESSURE_POS = 17
////    private val TEMP_POS = 21
////    private val BAT_POS = 25
////    private val LEAK_FLAG_POS = 26
//
//    var id: Int
////        private set(value) {
////            field = value
////        }
//
//    var lowPressureTh: Int
////        private set(value) {
////            field = value
////        }
//
//    @Ignore
//    var pressureBar: Int? //= null
////        private set(value) {
////            field = value
////        }
//
//    @Ignore
//    var temperatureC: Int? //= null
////        private set(value) {
////            field = value
////        }
//
//    @Ignore
//    var battery: Byte? = null
////        private set(value) {
////            field = value
////        }
//
//    init {
//        id = _id
//        lowPressureTh = _lowPressureTh
//        pressureBar = null
//        temperatureC = null
//        battery = null
//    }
//
//    //@JvmOverloads
//
//    constructor(id: Int, lowPressureTh: Int, pressureBar: Int?, temperatureC: Int?, battery: Byte?) : this(id, lowPressureTh)
data class Sensor(
    var id: Int,
    var lowPressureTh: Int,
    @Ignore
    var pressureBar: Int? = null,
    @Ignore
    var temperatureC: Int? = null,
    @Ignore
    var battery: Byte? = null
) {

    constructor(id: Int, lowPressureTh: Int) : this(id, lowPressureTh, null, null, null)

    @Ignore
    public fun updateMeasurementFromAdvData(advData: List<Long>) {
        pressureBar = decodeInt(advData[0].toInt())
        temperatureC = decodeInt(advData[1].toInt())
        battery = advData[2].toByte()
        //leakAlert = when (advData[3]) {0.toLong() -> false; else -> true}
        Log.d("Moje", "pressure:" + pressureBar.toString())
        Log.d("Moje", "temp:" +temperatureC.toString())
        Log.d("Moje", "bat:" +battery.toString())

    }

    @Ignore
    private fun decodeInt(codedValue: Int) : Int {
        val buffer = ByteBuffer.allocate(4)
        buffer.putInt(codedValue)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.flip()
        return buffer.int
    }

    @Ignore
    public fun equalId(id3: Int, id2: Int, id1: Int): Boolean {
        if (id3 <= 0xFF && id2 <= 0xFF && id1 <= 0xFF) {
            return (id == id3.shl(16).or(id2.shl(8).or(id1)))
        }
        else {
            return false
        }
    }

    @Ignore
    fun setToNullData() {
        pressureBar = null
        temperatureC = null
        battery = null
    }
}