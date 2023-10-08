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
    fun isPressureLow() : Boolean {
        val pressure = pressureBar
        return if (pressure != null) {
            (pressure < lowPressureTh)
        }
        else {
            false
        }
    }
    @Ignore
    public fun updateDataIfDetected(beacon: Beacon): Boolean {
        return try {
            if (equalId(beacon.id1.toInt(), beacon.id2.toInt(), beacon.id3.toInt())) {
                updateMeasurementFromAdvData(beacon.dataFields, ProtocolVer.from(beacon.beaconTypeCode))
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    @Ignore
    private fun updateMeasurementFromAdvData(advData: List<Long>, protocolVer: ProtocolVer) {
        pressureBar = if (protocolVer == ProtocolVer.OLD) { advData[0].toInt() / 100 } else { advData[0].toInt() }
        temperatureC = advData[1].toInt()
        battery = advData[2].toByte()

        //Log.d("Moje", "pressure:" + pressureBar.toString())
        //Log.d("Moje", "temp:" + temperatureC.toString())
        //Log.d("Moje", "bat:" + battery.toString())
    }



    @Ignore
    private fun equalId(id3: Int, id2: Int, id1: Int): Boolean {
        return if (id3 <= 0xFF && id2 <= 0xFF && id1 <= 0xFF) {
            (id == id3.shl(16).or(id2.shl(8).or(id1)))
        } else {
            false
        }
    }

    @Ignore
    fun setToNullData() {
        pressureBar = null
        temperatureC = null
        battery = null
    }
}