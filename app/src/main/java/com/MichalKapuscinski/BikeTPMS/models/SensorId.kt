package com.MichalKapuscinski.BikeTPMS.models

class SensorId {
    enum class WhichWheel
    {
        FRONT_LEFT,
        FRONT_RIGHT,
        REAR_LEFT,
        REAR_RIGHT,
    }

    private val myIntArray: IntArray = intArrayOf()
    private val whichWheel: WhichWheel = WhichWheel.FRONT_LEFT

}