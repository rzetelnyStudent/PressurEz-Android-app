package com.MichalKapuscinski.BikeTPMS.disk.storage

import android.content.Context
import androidx.room.Room
import com.MichalKapuscinski.BikeTPMS.models.Bike

class DiskStorage
{
    private val db: BikeDatabase
    constructor(context: Context) {
        db = Room.databaseBuilder(context, BikeDatabase::class.java,"bikedb")
            .allowMainThreadQueries().build()

    }

    public fun readBikesFromDisk(): List<Bike> {
        return db.dao.selectBikes()
        //bikeList.add(Bike(bikeList.size, "Test", R.drawable.ic_bike, 1, 4, 1000, 1000))
        //bikeList.add(Bike(bikeList.size, "MTB", R.drawable.ic_bike, 1, 4, 1000, 1000))
        //bikeList.add(Bike(bikeList.size, "SZOSa", R.drawable.ic_bike, 1, 4, 1000, 1000))
    }

    // remember to read bikes form disk after saving!!!
    public fun saveBikeOnDisk(bike: Bike): Boolean {
        db.dao.upsertBike(bike)     // exceptions!!!
        return true
    }

    public fun deleteBike(bike: Bike) {
        db.dao.deleteBike(bike)
    }
}