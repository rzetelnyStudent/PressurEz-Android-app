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
        cardCellBinding.pressureFront.text = formatNullablePressure(bike.sensorFront.pressureBar)
        cardCellBinding.temperatureFront.text = formatNullableTemp(bike.sensorFront.temperatureC)
        cardCellBinding.batteryFront.text = formatNullableBat(bike.sensorFront.battery)

        cardCellBinding.pressureRear.text = formatNullablePressure(bike.sensorRear.pressureBar)
        cardCellBinding.temperatureRear.text = formatNullableTemp(bike.sensorRear.temperatureC)
        cardCellBinding.batteryRear.text = formatNullableBat(bike.sensorRear.battery)
    }

    private fun formatNullablePressure(value: Int?) : String {
        return if (value == null) {
            "-,--"
        } else {
            String.format("%.2f", (value.toDouble() / 100000))
        }
    }

    private fun formatNullableTemp(value: Int?) : String {
        return if (value == null) {
            "--,-"
        } else {
            String.format("%.1f", (value.toDouble() / 100))
        }
    }

    private fun formatNullableBat(value: Byte?) : String {
        return value?.toString() ?: "--"
    }
}