package com.MichalKapuscinski.BikeTPMS.disk.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.MichalKapuscinski.BikeTPMS.models.Bike

@Database (entities = [Bike::class], version = 2)
abstract class BikeDatabase: RoomDatabase() {
    abstract val dao: BikeDao

}