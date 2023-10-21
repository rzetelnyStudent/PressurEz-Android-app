package com.MichalKapuscinski.BikeTPMS.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.MichalKapuscinski.BikeTPMS.MainActivity
import com.MichalKapuscinski.BikeTPMS.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

//object Navigation {

    fun MainActivity.linkToSettingsIfBtOff(btOn: Boolean) {
        if (!btOn) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.turn_ble_title)
            .setMessage(R.string.turn_ble_message)
            .setIcon(R.drawable.baseline_bluetooth_disabled_24)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                val settingsIntent = Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)
                startActivity(settingsIntent)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
        }
    }
//}