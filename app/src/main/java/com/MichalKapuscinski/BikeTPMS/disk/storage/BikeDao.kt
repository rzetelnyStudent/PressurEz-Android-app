package com.MichalKapuscinski.BikeTPMS.disk.storage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.MichalKapuscinski.BikeTPMS.models.Bike

@Dao
interface BikeDao {

    @Upsert
    fun upsertBike(bike: Bike)

    @Delete
    fun deleteBike(bike: Bike)

    @Query("SELECT * FROM bikes")
    fun selectBikes() : List<Bike>
}