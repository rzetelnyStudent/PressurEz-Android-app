package com.MichalKapuscinski.BikeTPMS

import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder


class Sensor {

    private val PRESSURE_POS = 17
    private val TEMP_POS = 21
    private val BAT_POS = 25
    private val LEAK_FLAG_POS = 26

    private var pressureBar: Int = 0
    private var temperatureC: Int = 0
    private var battery: Byte = 0
    private var leakAlert: Boolean = false

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
        val flippedBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.flip()
        return buffer.getInt()
    }

}