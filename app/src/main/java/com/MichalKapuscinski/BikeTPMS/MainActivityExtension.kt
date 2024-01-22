package com.MichalKapuscinski.BikeTPMS

import android.content.Intent
import com.MichalKapuscinski.BikeTPMS.models.LatchedFalseBool
import com.MichalKapuscinski.BikeTPMS.permissions.PermissionGroup
import com.MichalKapuscinski.BikeTPMS.permissions.PermissionsHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder


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


fun MainActivity.promptConditionallyForBackgroundLocation() {
    val permission = PermissionsHelper.isNeededNotGrantedBackgroundLoc()
    if (permission != null) {
        if (shouldShowRequestPermissionRationale(permission)) {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.location_request_title)
                .setMessage(R.string.location_request_message)
                .setIcon(R.drawable.baseline_location_on_24)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                    requestPermissionsLauncher.launch(arrayOf(permission))
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}


fun MainActivity.promptConditionallyForPermissions() {
    val notGrantedPermissions = PermissionsHelper.getNotGrantedRationalePermissions(this, PermissionGroup.ALL)
    if (notGrantedPermissions.isNotEmpty()) {
        val showRationaleForAll = LatchedFalseBool()
        for (permission in notGrantedPermissions) {
            showRationaleForAll.registerState(shouldShowRequestPermissionRationale(permission))
        }
        if (showRationaleForAll.isCorrect()) {
            requestPermissionsLauncher.launch(notGrantedPermissions)
        }
    }
}