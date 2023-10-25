package com.MichalKapuscinski.BikeTPMS.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.MichalKapuscinski.BikeTPMS.models.Bike
import com.MichalKapuscinski.BikeTPMS.models.ValidationInfo

object PermissionsHelper {
    // Manifest.permission.ACCESS_BACKGROUND_LOCATION
    // Manifest.permission.ACCESS_FINE_LOCATION
    // Manifest.permission.BLUETOOTH_SCAN
    // Manifest.permission.POST_NOTIFICATIONS

    private fun isPermissionGranted(context: Context, permissionString: String): Boolean {
        return (ContextCompat.checkSelfPermission(context, permissionString) == PackageManager.PERMISSION_GRANTED)
    }
//    fun setFirstTimeAskingPermission(permissionString: String, isFirstTime: Boolean) {
//        val sharedPreference = context.getSharedPreferences("org.altbeacon.permisisons",
//            AppCompatActivity.MODE_PRIVATE
//        )
//        sharedPreference.edit().putBoolean(permissionString,
//            isFirstTime).apply()
//    }

//    fun isFirstTimeAskingPermission(permissionString: String): Boolean {
//        val sharedPreference = context.getSharedPreferences(
//            "org.altbeacon.permisisons",
//            AppCompatActivity.MODE_PRIVATE
//        )
//        return sharedPreference.getBoolean(
//            permissionString,
//            true
//        )
//    }

    private fun getScanningPermissions(): Array<String> {
        val permissions = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // As of version M (6) we need FINE_LOCATION (or COARSE_LOCATION, but we ask for FINE)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // As of version Q (10) we need FINE_LOCATION and BACKGROUND_LOCATION
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // As of version S (12) we need FINE_LOCATION, BLUETOOTH_SCAN and BACKGROUND_LOCATION
            // Manifest.permission.BLUETOOTH_CONNECT is not absolutely required to do just scanning,
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        return permissions.toTypedArray()
    }

    private fun getNotificationPermissions(): Array<String> {
        val permissions = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return permissions.toTypedArray()
    }

    private fun getListedPermissions(group: PermissionGroup): Array<String> {
        return when (group) {
            PermissionGroup.SCANNING -> getScanningPermissions()
            PermissionGroup.NOTIFICATIONS -> getNotificationPermissions()
        }
    }


    fun getNotGrantedPermissions(context: Context, group: PermissionGroup): Array<String> {
        val permissions = getListedPermissions(group)
        val notGrantedPermissions = ArrayList<String>()
        for (permission in permissions) {
            if (!isPermissionGranted(context, permission)) {
                notGrantedPermissions.add(permission)
            }
        }
        return notGrantedPermissions.toTypedArray()
    }


    fun allPermissionsGranted(context: Context, group: PermissionGroup): Boolean {
        val permissions = getListedPermissions(group)
        val grantingInfo = ValidationInfo()
        for (permission in permissions) {
            grantingInfo.registerState(isPermissionGranted(context, permission))
        }
        return grantingInfo.isCorrect()
    }
}