package com.MichalKapuscinski.BikeTPMS.ui

import androidx.recyclerview.widget.RecyclerView
import com.MichalKapuscinski.BikeTPMS.BikeClickListener
import com.MichalKapuscinski.BikeTPMS.R
import com.MichalKapuscinski.BikeTPMS.databinding.CardCellBinding
import com.MichalKapuscinski.BikeTPMS.models.Bike

class CardViewHolder(
    private val cardCellBinding: CardCellBinding,
    private val clickListener: BikeClickListener
) : RecyclerView.ViewHolder(cardCellBinding.root) {
    fun bindBike(bike: Bike) {
        cardCellBinding.bikeIcon.setImageResource(R.drawable.ic_bike)
        cardCellBinding.bikeName.text = bike.name
        cardCellBinding.pressureFront.text = formatNullablePressure(bike.sensorFront.pressureBar)
        cardCellBinding.temperatureFront.text = formatNullableTemp(bike.sensorFront.temperatureC)
        cardCellBinding.batteryFront.text = formatNullableBat(bike.sensorFront.battery)

        cardCellBinding.pressureRear.text = formatNullablePressure(bike.sensorRear.pressureBar)
        cardCellBinding.temperatureRear.text = formatNullableTemp(bike.sensorRear.temperatureC)
        cardCellBinding.batteryRear.text = formatNullableBat(bike.sensorRear.battery)
        cardCellBinding.cardView.setOnClickListener{
            clickListener.onClick(bike)
        }
    }
}