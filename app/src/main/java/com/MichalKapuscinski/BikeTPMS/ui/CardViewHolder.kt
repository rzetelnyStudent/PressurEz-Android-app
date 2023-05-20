package com.MichalKapuscinski.BikeTPMS.ui

import androidx.recyclerview.widget.RecyclerView
import com.MichalKapuscinski.BikeTPMS.databinding.CardCellBinding
import com.MichalKapuscinski.BikeTPMS.models.Bike

class CardViewHolder(
    private val cardCellBinding: CardCellBinding
) : RecyclerView.ViewHolder(cardCellBinding.root) {
    fun bindBike(bike: Bike) {
        cardCellBinding.bikeIcon.setImageResource(bike.appearance)
        cardCellBinding.bikeName.text = bike.name
        cardCellBinding.pressureFront.text = String.format("%.2f", (bike.sensorFront.pressureBar.toDouble() / 100000))
        cardCellBinding.temperatureFront.text = String.format("%.1f", (bike.sensorFront.temperatureC.toDouble() / 100))
        cardCellBinding.batteryFront.text = bike.sensorFront.battery.toString()

        cardCellBinding.pressureRear.text = String.format("%.2f", (bike.sensorRear.pressureBar.toDouble() / 100000))
        cardCellBinding.temperatureRear.text = String.format("%.1f", (bike.sensorRear.temperatureC.toDouble() / 100))
        cardCellBinding.batteryRear.text = bike.sensorRear.battery.toString()
    }
}