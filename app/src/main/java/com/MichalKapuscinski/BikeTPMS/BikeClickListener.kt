package com.MichalKapuscinski.BikeTPMS

import com.MichalKapuscinski.BikeTPMS.models.Bike

interface BikeClickListener {
    fun onClick(bike: Bike)
}