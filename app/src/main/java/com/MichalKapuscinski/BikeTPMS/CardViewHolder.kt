package com.MichalKapuscinski.BikeTPMS

import androidx.recyclerview.widget.RecyclerView
import com.MichalKapuscinski.BikeTPMS.databinding.CardCellBinding

class CardViewHolder(
    private val cardCellBinding: CardCellBinding
) : RecyclerView.ViewHolder(cardCellBinding.root) {
    fun bindBike(bike: Bike) {
        cardCellBinding.bikeIcon.setImageResource(bike.appearance)
        cardCellBinding.bikeName.text = bike.name
        cardCellBinding.pressure.text = bike.pressure.toString()
    }
}