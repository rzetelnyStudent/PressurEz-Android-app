package com.MichalKapuscinski.BikeTPMS

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.util.Log
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
        requestPermissionsLauncher.launch(notGrantedPermissions)
//        val showRationale = shouldShowRequestPermissionRationale(firstPermission)
//        if (showRationale ||  PermissionsHelper(this).isFirstTimeAskingPermission(firstPermission)) {
//            PermissionsHelper(this).setFirstTimeAskingPermission(firstPermission, false)
//            requestPermissionsLauncher.launch(permissionsGroup)
//        }
//        else {
//            val builder = AlertDialog.Builder(this)
//            builder.setTitle("Can't request permission")
//            builder.setMessage("This permission has been previously denied to this app.  In order to grant it now, you must go to Android Settings to enable this permission.")
//            builder.setPositiveButton("OK", null)
//            builder.show()
//        }
    }
}