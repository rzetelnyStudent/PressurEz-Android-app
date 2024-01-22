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