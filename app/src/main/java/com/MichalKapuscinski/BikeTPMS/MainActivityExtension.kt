package com.MichalKapuscinski.BikeTPMS

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.MichalKapuscinski.BikeTPMS.models.ValidationInfo
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


fun MainActivity.promptConditionallyForPermissions() {
    val notGrantedPermissions = PermissionsHelper.getNotGrantedPermissions(this, PermissionGroup.SCANNING)
    if (notGrantedPermissions.isNotEmpty()) {
        val showRationaleForAll = ValidationInfo()
        for (permission in notGrantedPermissions) {
            showRationaleForAll.registerState(shouldShowRequestPermissionRationale(notGrantedPermissions.first()))
        }
        if (showRationaleForAll.isCorrect()) {
            requestPermissionsLauncher.launch(notGrantedPermissions)
        }
    }
}