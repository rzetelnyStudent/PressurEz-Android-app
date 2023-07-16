package com.MichalKapuscinski.BikeTPMS.scanner

import com.MichalKapuscinski.BikeTPMS.R
import com.MichalKapuscinski.BikeTPMS.models.Bike

class DiskStorage
{
    public fun initialize() {

    }

    public fun readSensorsFromDisk(bikeList: MutableList<Bike>) {
        bikeList.add(Bike("Zimówka", R.drawable.ic_bike, bikeList.size, 1, 4, 1000, 1000))
        bikeList.add(Bike("MTB", R.drawable.ic_bike, bikeList.size, 1, 4, 1000, 1000))
        bikeList.add(Bike("SZOSa", R.drawable.ic_bike, bikeList.size, 1, 4, 1000, 1000))
    }

    public fun saveSensorsOnDisk(bikeList: MutableList<Bike>) {

    }
}