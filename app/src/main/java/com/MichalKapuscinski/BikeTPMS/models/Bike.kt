package com.MichalKapuscinski.BikeTPMS.models

//var bikeList = mutableListOf<Bike>()

class Bike(name: String, appearance: Int, id: Int, sensorFid: Int, sensorRid: Int, lowPressThreshF: Int, lowPressThreshR: Int) {

    var appearance: Int
    var name: String
    var sensorFront: Sensor
    var sensorRear: Sensor
    private val id: Int
    init {
        this.name = name
        this.appearance = appearance
        this.id = id
        sensorFront = Sensor(sensorFid, lowPressThreshF)
        sensorRear = Sensor(sensorRid, lowPressThreshR)
    }

}