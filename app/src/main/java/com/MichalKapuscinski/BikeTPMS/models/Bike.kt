package com.MichalKapuscinski.BikeTPMS.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bikes")
data class Bike(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String,
    var appearance: Int,
    @Embedded(prefix = "senF_")
    val sensorFront: Sensor,
    @Embedded(prefix = "senR_")
    val sensorRear: Sensor
) {

    constructor(id: Int, name: String, appearance: Int, sensorFid: Int, sensorRid: Int, lowPressThreshF: Int, lowPressThreshR: Int):
            this(id, name, appearance, Sensor(sensorFid, lowPressThreshF), Sensor(sensorRid, lowPressThreshR))
}

//@Entity(tableName = "bikes")
//class Bike(name: String, appearance: Int, id: Int, sensorFid: Int, sensorRid: Int, lowPressThreshF: Int, lowPressThreshR: Int) {
//
//    var appearance: Int = appearance
//    var name: String = name
//    @Embedded(prefix = "senF_")
//    val sensorFront: Sensor = Sensor(sensorFid, lowPressThreshF)
//    @Embedded(prefix = "senR_")
//    val sensorRear: Sensor = Sensor(sensorRid, lowPressThreshR)
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = id
//
////    init {
////        this.name = name
////        this.appearance = appearance
////        this.id = id
////        sensorFront = Sensor(sensorFid, lowPressThreshF)
////        sensorRear = Sensor(sensorRid, lowPressThreshR)
////    }
//
////    constructor(name: String,
////                appearance: Int,
////                id: Int,
////                sensorFid: Int,
////                sensorRid: Int,
////                lowPressThreshF: Int,
////                lowPressThreshR: Int): this(name, appearance, id, sensorFid, sensorRid, lowPressThreshF, lowPressThreshR, 0)
//
//}