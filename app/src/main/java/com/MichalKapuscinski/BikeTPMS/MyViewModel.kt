package com.MichalKapuscinski.BikeTPMS

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.MichalKapuscinski.BikeTPMS.models.Bike

class MyViewModel: ViewModel()
{
    var bikeAddedOrEdited = MutableLiveData<Pair<Bike?, Boolean>>()
//    var returningFromAddEditDialog = MutableLiveData<Boolean>()
//    var pair = Pair(bikeAddedOrEdited, returningFromAddEditDialog)
}