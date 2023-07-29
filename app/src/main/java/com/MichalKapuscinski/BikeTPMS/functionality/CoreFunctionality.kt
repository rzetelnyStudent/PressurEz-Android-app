package com.MichalKapuscinski.BikeTPMS.functionality

import android.app.*
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.MichalKapuscinski.BikeTPMS.MainActivity
import com.MichalKapuscinski.BikeTPMS.R
import com.MichalKapuscinski.BikeTPMS.disk.storage.DiskStorage
import com.MichalKapuscinski.BikeTPMS.models.Bike
import com.MichalKapuscinski.BikeTPMS.notifications.MyNotificationManager
import com.MichalKapuscinski.BikeTPMS.notifications.NotificationState
import org.altbeacon.beacon.*

class CoreFunctionality: Application(), DefaultLifecycleObserver {
    lateinit var region: Region
    private lateinit var beaconManager: BeaconManager
    private lateinit var bleScanner: BleScanner
    private lateinit var diskStorage: DiskStorage
    private lateinit var myNotificationManager: MyNotificationManager
    var bikeList = mutableListOf<Bike>()

    var notificationCreated = false

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        //Log.d("aa", "onStart: $owner")
        bleScanner.stopBackgroundStartForegroundScan()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        //Log.d("aa", "onStop: $owner")
        bleScanner.stopForegroundStartBackgroundScan()
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
        bleScanner.startBackgroundScan(this.centralRangingObserver, this.centralMonitoringObserver)
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
            for (bike in bikeList) {
                bike.notificationState = NotificationState.NOT_VISIBLE    // there could be code like this:
                //if (NotificationState.VISIBLE) -> {
                ///    if (myNotificationManager.isNotificationVisible(bike)) {
                //        myNotificationManager.sendNotification(bike)     // actually update a notification
                //    }
                //    else {     // it means the notification was dismissed
                //        bike.notificationState = NotificationState.NOT_VISIBLE_DISMISSED
                //    }
                // tutaj powinno byc tez wynullowane data w powiadomieniu.
                // tutajpowinien sie znalezc kod, ktory z dismissed zrobi not_visible (zresetuje dismissa)
            }
        }
        else {
            Log.d("aaa", "inside beacon region: ")
            //when (notificationCreated) {false->sendNotification(); true->updateNotification()}

        }
    }


    private val centralRangingObserver = Observer<Collection<Beacon>> { sensors ->
        // Log.d("aaa", "Ranged: ${sensors.count()} beacons")
        for (bike: Bike in bikeList) {
            val sensorDetected = booleanArrayOf(false, false)
            for (sensorF in sensors) {
                if (bike.sensorFront.equalId(sensorF.id1.toInt(), sensorF.id2.toInt(), sensorF.id3.toInt())) {    // exceptions!!!!
                    bike.sensorFront.updateMeasurementFromAdvData(sensorF.dataFields)
                    sensorDetected[0] = true
                }
            }
            // here logically could be: if (!sensorDetected[0]) { bike.sensorFront.setToNullData() }, but packets are lost sometimes
            // it would be visible to user then
            for (sensorR in sensors) {
                if (bike.sensorRear.equalId(sensorR.id1.toInt(), sensorR.id2.toInt(), sensorR.id3.toInt())) {    // exceptions!!!!
                    bike.sensorRear.updateMeasurementFromAdvData(sensorR.dataFields)
                    sensorDetected[1] = true
                }
            }
            // here logically could be: if (!sensorDetected[1]) { bike.sensorRear.setToNullData() }, but packets are lost sometimes
            // it would be visible to user then
            if (sensorDetected[0] || sensorDetected[1]) {
                when (bike.notificationState) {
                    NotificationState.NOT_VISIBLE -> {
                        myNotificationManager.sendNotification(bike)
                        bike.notificationState = NotificationState.VISIBLE
                    }
                    NotificationState.VISIBLE -> {
                        if (myNotificationManager.isNotificationVisible(bike)) {
                            myNotificationManager.sendNotification(bike)     // actually update a notification
                        } else {     // it means the notification was dismissed
                            bike.notificationState = NotificationState.NOT_VISIBLE_DISMISSED
                        }
                    }
                    NotificationState.NOT_VISIBLE_DISMISSED -> {}     // could check here if some sort of timeout passed since the notification was dismissed
                }
            }
        }
    }

    public fun addEditBike(name: String, fSensorId: Int, rSensorId: Int, lowPressureThreshF: Int, lowPressureThreshR: Int) {
        //bikeList.add(Bike(bikeList.size, "Zimówka", R.drawable.ic_bike, fSensorId, rSensorId, lowPressureThreshF, lowPressureThreshR))
        diskStorage.saveBikeOnDisk(Bike(0, name, R.drawable.ic_bike, fSensorId, rSensorId, lowPressureThreshF, lowPressureThreshR))
        bikeList = diskStorage.readBikesFromDisk() as MutableList<Bike>
        // toast???
    }

    var notificationId = 0
    var pressure = 0.0

    private fun sendNotification() {
        val builder = NotificationCompat.Builder(this, "beacon-ref-notification-id")
            .setSmallIcon(R.drawable.ic_stat_bike)
            .setContentTitle("Zimówka")
            .setContentText("Rear: " + "1,34" + "\t" + "Front: " + "2,45")
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(Intent(this, MainActivity::class.java))
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(resultPendingIntent)

        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
        notificationCreated = true
        //with(NotificationManagerCompat.from(context)) {
        //    notify(notificationId, builder.build())
        //}
    }

    private fun updateNotification() {
        pressure += 0.1
        val builder = NotificationCompat.Builder(this, "beacon-ref-notification-id")
            .setSmallIcon(R.drawable.ic_stat_bike)
            .setContentTitle("Zimówka")
            .setContentText("Rear: " + pressure.toString() + "     " + "Front: " + "2,45")
        //.setPriority(NotificationCompat.PRIORITY_HIGH)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(Intent(this, MainActivity::class.java))
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(resultPendingIntent)

        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
        notificationCreated = true
    }

    companion object {
        val TAG = "BeaconReference"
    }

}