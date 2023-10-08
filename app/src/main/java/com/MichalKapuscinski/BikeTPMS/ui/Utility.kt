package com.MichalKapuscinski.BikeTPMS.ui

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import com.MichalKapuscinski.BikeTPMS.R

const val MAX_BIKE_NAME_LENGTH = 20
const val ID_LENGTH = 6
const val INT_ERROR = -1
const val MBAR_IN_BAR = 1000
val PRESSURE_RANGE = 0..6000

fun formatNullablePressure(value: Int?) : String {
    return if (value == null) {
        "-,--"
    } else {
        String.format("%.2f", (value.toDouble() / MBAR_IN_BAR))
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

fun validateBikeName(name: String?, resources: Resources) : Pair<String?, String> {
    return if (name.isNullOrEmpty()) {
        Pair(resources.getString(R.string.empty_bike_name), "")
    }
    else if (name.length > MAX_BIKE_NAME_LENGTH) {
        Pair(resources.getString(R.string.too_long_name), "")
    }
    else {
        Pair(null, name)
    }
}

fun validateSensorId(idString: String?, resources: Resources): Pair<String?, Int> {
    return if (!idString.isNullOrEmpty()) {
        if (idString.length == ID_LENGTH) {
            try {
                val idInt = convertStringIdToInt(idString)
                Pair(null, idInt)
            } catch (e: NumberFormatException) {
                Pair(resources.getString(R.string.wrong_format_id), INT_ERROR)
            }
        } else {
            Pair(resources.getString(R.string.wrong_length_id), INT_ERROR)
        }
    } else {
        Pair(resources.getString(R.string.wrong_length_id), INT_ERROR)
    }
}

fun convertStringIdToInt(idString: String): Int {
    try {
        return idString.toInt(16)
    } catch (_ :IllegalArgumentException) {
        throw NumberFormatException()
    } catch (e :NumberFormatException) {
        throw e
    }
}

fun validateLowPressureThreshold(inString: String?, resources: Resources): Pair<String?, Int> {
    return if (!inString.isNullOrEmpty()) {
        try {
            val pressureMBar = (inString.toDouble() * MBAR_IN_BAR).toInt()      // convert to mBar
            if (pressureMBar in PRESSURE_RANGE) {
                Pair(null, pressureMBar)
            } else {
                Pair(resources.getString(R.string.threshold_invalid_value), INT_ERROR)
            }
        } catch (e: NumberFormatException) {
            Pair(resources.getString(R.string.threshold_invalid_value), INT_ERROR)
        }
    } else {
        Pair(resources.getString(R.string.empty_field), INT_ERROR)
    }
}

fun formatId(id: Int): String
{
    return String.format("%06X", id)
}

fun formatLowPressure(value: Int): String
{
    return String.format("%.2f", (value.toDouble() / MBAR_IN_BAR))
}

fun Context.resourceUri(resourceId: Int): Uri = with(resources) {
    Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(getResourcePackageName(resourceId))
        .appendPath(getResourceTypeName(resourceId))
        .appendPath(getResourceEntryName(resourceId))
        .build()
}

