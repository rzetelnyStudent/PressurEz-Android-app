package com.MichalKapuscinski.BikeTPMS

import androidx.recyclerview.widget.RecyclerView
import com.MichalKapuscinski.BikeTPMS.databinding.CardCellBinding

class CardViewHolder(
    private val cardCellBinding: CardCellBinding
) : RecyclerView.ViewHolder(cardCellBinding.root) {
    fun bindBike(bike: Bike) {
        cardCellBinding.bikeIcon.setImageResource(bike.appearance)
        cardCellBinding.bikeName.text = bike.name
        cardCellBinding.pressureFront.text = String.format("%.2f", (bike.sensorFront.pressureBar.toDouble() / 100000))
        cardCellBinding.temperatureFront.text = String.format("%.2f", (bike.sensorFront.temperatureC.toDouble() / 100))
        cardCellBinding.batteryFront.text = bike.sensorFront.toString()

        cardCellBinding.pressureRear.text = String.format("%.2f", (bike.sensorRear.pressureBar.toDouble() / 100000))
        cardCellBinding.temperatureRear.text = String.format("%.2f", (bike.sensorRear.temperatureC.toDouble() / 100))
        cardCellBinding.batteryRear.text = bike.sensorRear.toString()
    }
}