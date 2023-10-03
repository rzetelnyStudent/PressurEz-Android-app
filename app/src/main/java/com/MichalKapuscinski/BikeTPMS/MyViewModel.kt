package com.MichalKapuscinski.BikeTPMS

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.MichalKapuscinski.BikeTPMS.models.Bike
import com.MichalKapuscinski.BikeTPMS.models.NavigationInfo

class MyViewModel: ViewModel()
{
    var navInfo = MutableLiveData<NavigationInfo>()
//    var returningFromAddEditDialog = MutableLiveData<Boolean>()
//    var pair = Pair(bikeAddedOrEdited, returningFromAddEditDialog)
}