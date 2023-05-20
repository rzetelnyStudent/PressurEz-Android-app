package com.MichalKapuscinski.BikeTPMS.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.MichalKapuscinski.BikeTPMS.databinding.CardCellBinding
import com.MichalKapuscinski.BikeTPMS.models.Bike

class CardAdapter(private val bikes: List<Bike>): RecyclerView.Adapter<CardViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardCellBinding.inflate(from, parent, false)
        return CardViewHolder(binding)
    }

    override fun getItemCount(): Int = bikes.size


    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindBike(bikes[position])
    }

}