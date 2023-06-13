package com.MichalKapuscinski.BikeTPMS.scanner

import android.app.*
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner
import com.MichalKapuscinski.BikeTPMS.MainActivity
import com.MichalKapuscinski.BikeTPMS.R
import org.altbeacon.beacon.*

class BeaconReferenceApplication: Application(), DefaultLifecycleObserver {
    lateinit var region: Region
    private lateinit var beaconManager: BeaconManager

    var notificationCreated = false

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("aa", "onStart: $owner")
        beaconManager.stopRangingBeacons(region)
        beaconManager.stopMonitoring(region)
        beaconManager.setIntentScanningStrategyEnabled(false)
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundScanPeriod(1100);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)

    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("aa", "onStop: $owner")

        beaconManager.stopRangingBeacons(region)
        beaconManager.stopMonitoring(region)
        beaconManager.setIntentScanningStrategyEnabled(true)
        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)
        var a = 0
    }

    //lateinit var appLifecycleObserver: AppLifecycleObserver
    //@Inject
    //lateinit var myAppLifecycleTracker: MyAppLifecycleTracker

    override fun onCreate() {
        super<Application>.onCreate()

        Log.d("aaa", "BeaconRef.onCreate()")
        //ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
        //lifecycle.addObserver(myLifecycleTracker)

        //val beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager = BeaconManager.getInstanceForApplication(this)
        BeaconManager.setDebug(true)

        // If you don't care about AltBeacon, you can clear it from the defaults:
        beaconManager.getBeaconParsers().clear()
        val beaconParser = BeaconParser().setBeaconLayout("m:3-4=eaca,i:5-5,i:6-6,i:7-7,p:2-2,d:8-11,d:12-15,d:16-16,d:17-17")
        beaconParser.setHardwareAssistManufacturerCodes(intArrayOf(0x0100))
        beaconManager.getBeaconParsers().add(beaconParser)   // has to contain 3 ids otherwise an exception is thrown

        // By default, the library will scan in the background every 5 minutes on Android 4-7,
        // which will be limited to scan jobs scheduled every ~15 minutes on Android 8+
        // If you want more frequent scanning (requires a foreground service on Android 8+),
        // configure that here.
        // If you want to continuously range beacons in the background more often than every 15 mintues,
        // you can use the library's built-in foreground service to unlock this behavior on Android
        // 8+.   the method below shows how you set that up.
        //setupForegroundService()
        //beaconManager.setEnableScheduledScanJobs(false)
        //beaconManager.setBackgroundScanPeriod(1100)
        //beaconManager.setBackgroundBetweenScanPeriod(0)

        // Ranging callbacks will drop out if no beacons are detected
        // Monitoring callbacks will be delayed by up to 25 minutes on region exit
        beaconManager.setIntentScanningStrategyEnabled(true)

        // The code below will start "monitoring" for beacons matching the region definition below
        // the region definition is a wildcard that matches all beacons regardless of identifiers.
        // if you only want to detect beacons with a specific UUID, change the id1 paremeter to
        // a UUID like Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6")
        region = Region("all-beacons", null, null, null)
        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)     // wysypuje apke
        // These two lines set up a Live Data observer so this Activity can get beacon data from the Application class
        val regionViewModel = BeaconManager.getInstanceForApplication(this).getRegionViewModel(region)
        // observer will be called each time the monitored regionState changes (inside vs. outside region)
        regionViewModel.regionState.observeForever( centralMonitoringObserver)
        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
        regionViewModel.rangedBeacons.observeForever( centralRangingObserver)
    }

    fun setupForegroundService() {
        val builder = Notification.Builder(this, "BeaconReferenceApp")
        //builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.setContentTitle("Scanning for Beacons")
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(pendingIntent);
        val channel =  NotificationChannel("beacon-ref-notification-id",
            "", NotificationManager.IMPORTANCE_DEFAULT)
        channel.setDescription("My Notification Channel Description")
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel);
        builder.setChannelId(channel.getId());

        BeaconManager.getInstanceForApplication(this).enableForegroundServiceScanning(builder.build(), 456);
    }

    val centralMonitoringObserver = Observer<Int> { state ->
        if (state == MonitorNotifier.OUTSIDE) {
            Log.d("aaa", "outside beacon region: "+region)
        }
        else {
            Log.d("aaa", "inside beacon region: "+region)
            //when (notificationCreated) {false->sendNotification(); true->updateNotification()}
            sendNotification()
        }
    }


    val centralRangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d("aaa", "Ranged: ${beacons.count()} beacons")
        // notificationManager.notify(notificationId, builder.build())
        //if (notificationCreated) {
        //    updateNotification()
        //} //else {
        //    sendNotification()
        //}
        for (beacon: Beacon in beacons) {
            Log.d("aaa", "$beacon about ${beacon.distance} meters away")
        }
    }

    var notificationId = 0
    var pressure = 0.0

    private fun sendNotification() {
        val builder = NotificationCompat.Builder(this, "beacon-ref-notification-id")
            .setSmallIcon(R.drawable.ic_stat_bike)
            .setContentTitle("Zimówka")
            .setContentText("Rear: " + "1,34" + "\t" + "Front: " + "2,45")
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