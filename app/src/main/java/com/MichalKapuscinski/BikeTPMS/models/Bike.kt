package com.MichalKapuscinski.BikeTPMS.models

var bikeList = mutableListOf<Bike>()

class Bike (

    var appearance: Int,
    var name: String,
    var sensorFront: Sensor,
    var sensorRear: Sensor,
    val id: Int? = bikeList.size
)