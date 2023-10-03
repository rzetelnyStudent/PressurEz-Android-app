package com.MichalKapuscinski.BikeTPMS.models

enum class Action {
    NOTHING, ADDED_OR_EDITED, DELETED
}

data class NavigationInfo (
    var bikeAddedOrEdited: Bike?,
    var action: Action
)
