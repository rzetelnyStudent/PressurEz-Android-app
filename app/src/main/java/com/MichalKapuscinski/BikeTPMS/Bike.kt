package com.MichalKapuscinski.BikeTPMS

var bikeList = mutableListOf<Bike>()

class Bike (

    var appearance: Int,
    var name: String,
    var pressure: Int,
    var battery: Int,
    val id: Int? = bikeList.size
)