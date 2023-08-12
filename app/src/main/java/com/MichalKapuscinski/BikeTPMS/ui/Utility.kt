package com.MichalKapuscinski.BikeTPMS.ui

fun formatNullablePressure(value: Int?) : String {
    return if (value == null) {
        "-,--"
    } else {
        String.format("%.2f", (value.toDouble() / 100000))
    }
}

fun formatNullableTemp(value: Int?) : String {
    return if (value == null) {
        "--,-"
    } else {
        String.format("%.1f", (value.toDouble() / 100))
    }
}

fun formatNullableBat(value: Byte?) : String {
    return value?.toString() ?: "--"
}