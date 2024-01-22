package com.MichalKapuscinski.BikeTPMS.functionality

import android.app.*
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.MichalKapuscinski.BikeTPMS.R
import com.MichalKapuscinski.BikeTPMS.disk.storage.DiskStorage
import com.MichalKapuscinski.BikeTPMS.models.Bike
import com.MichalKapuscinski.BikeTPMS.models.ValidationInfo
import com.MichalKapuscinski.BikeTPMS.notifications.MyNotificationManager
import com.MichalKapuscinski.BikeTPMS.permissions.PermissionGroup
import com.MichalKapuscinski.BikeTPMS.permissions.PermissionsHelper
import org.altbeacon.beacon.*

class CoreFunctionality: Application(), DefaultLifecycleObserver {
    lateinit var region: Region
    private lateinit var beaconManager: BeaconManager
    private lateinit var bleScanner: BleScanner
    private lateinit var diskStorage: DiskStorage
    private lateinit var myNotificationManager: MyNotificationManager
    var bikeList = mutableListOf<Bike>()
    var isForeground = false

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        isForeground = true
        if (PermissionsHelper.allPermissionsGranted(this, PermissionGroup.SCANNING)) {
            bleScanner.startForegroundScan()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        isForeground = false
    }

    override fun onCreate() {
        super<Application>.onCreate()

        diskStorage = DiskStorage(this)
        bikeList = diskStorage.readBikesFromDisk() as MutableList<Bike>
        //addEditBike("ssss", 2, 2, 2, 2)
        //diskStorage.deleteBike(bikeList[0])
        //diskStorage.deleteBike(bikeList[1])

        myNotificationManager = MyNotificationManager(this, "Pressure notifications", "Notifications shown and updated when a bike is detected")
        beaconManager = BeaconManager.getInstanceForApplication(this)
        BeaconManager.setDebug(true)
        region = Region("all-beacons", null, null, null)
        bleScanner = BleScanner(beaconManager, region)
        bleScanner.registerObservers(this.centralRangingObserver, this.centralMonitoringObserver)
        if (PermissionsHelper.allPermissionsGranted(this, PermissionGroup.SCANNING)) {
            bleScanner.startBackgroundScan()
        }
    }

//    fun setupForegroundService() {
//        val builder = Notification.Builder(this, "BeaconReferenceApp")
//        //builder.setSmallIcon(R.drawable.ic_launcher_foreground)
//        builder.setContentTitle("Scanning for Beacons")
//        val intent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
//        )
//        builder.setContentIntent(pendingIntent);
//        val channel =  NotificationChannel("beacon-ref-notification-id",
//            "", NotificationManager.IMPORTANCE_DEFAULT)
//        channel.setDescription("My Notification Channel Description")
//        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
//
//        val notificationManager = getSystemService(
//                Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel);
//        builder.setChannelId(channel.getId());
//
//        BeaconManager.getInstanceForApplication(this).enableForegroundServiceScanning(builder.build(), 456);
//    }

    private val centralMonitoringObserver = Observer<Int> { state ->
        if (state == MonitorNotifier.OUTSIDE) {
            Log.d("aaa", "outside beacon region: ")
            //for (bike in bikeList) {
                //myNotificationManager.resetState(bike)   // sometimes sensor signal is dropped, then it would make the app post again notification.
                // Even if it was dismissed
                // tutaj powinno byc tez wynullowane data w powiadomieniu. Albo moze w sumie nie nullowac niech zostanie stara wartosc w powiadomieniu
                // tutajpowinien sie znalezc kod, ktory z dismissed zrobi not_visible (zresetuje dismissa)
            //}
        }
    }


    private val centralRangingObserver = Observer<Collection<Beacon>> { sensors ->
        // Log.d("aaa", "Ranged: ${sensors.count()} beacons")
        for (bike: Bike in bikeList) {
            val sensorDetected = ValidationInfo()
            for (sensorF in sensors) {
                sensorDetected.registerState(bike.sensorFront.updateDataIfDetected(sensorF))
            }
            for (sensorR in sensors) {
                sensorDetected.registerState(bike.sensorRear.updateDataIfDetected(sensorR))
            }
            if (sensorDetected.isCorrect()) {
                myNotificationManager.postNotificationConditionally(bike, isForeground, this)
            }
        }
    }

    fun addEditBike(name: String, fSensorId: Int, rSensorId: Int, lowPressureThreshF: Int, lowPressureThreshR: Int) {
        //bikeList.add(Bike(bikeList.size, "Zimówka", R.drawable.ic_bike, fSensorId, rSensorId, lowPressureThreshF, lowPressureThreshR))
        diskStorage.saveBikeOnDisk(Bike(0, name, R.drawable.ic_bike, fSensorId, rSensorId, lowPressureThreshF, lowPressureThreshR))
        bikeList = diskStorage.readBikesFromDisk() as MutableList<Bike>
        // toast???
    }

    fun addEditBike(bike: Bike) {
        diskStorage.saveBikeOnDisk(bike)
        bikeList = diskStorage.readBikesFromDisk() as MutableList<Bike>
    }

    fun deleteBike(bike: Bike) {
        diskStorage.deleteBike(bike)
        bikeList = diskStorage.readBikesFromDisk() as MutableList<Bike>
    }

    fun isBleEnabled(): Boolean {
        return bleScanner.isBleEnabled()
    }

    fun startScan() {
        bleScanner.stop()
        bleScanner.startForegroundScan()
    }

    companion object {
        val TAG = "BeaconReference"
    }

}