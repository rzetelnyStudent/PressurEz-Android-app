package com.MichalKapuscinski.BikeTPMS.functionality

import androidx.lifecycle.Observer
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BleNotAvailableException
import org.altbeacon.beacon.Region

class BleScanner(_beaconManager: BeaconManager, _region: Region) {

    private val beaconManager = _beaconManager
    private val region = _region
    private val MANUFACTURER_CODE = intArrayOf(0x0100)
    private val OLD_BEACON_LAYOUT = "m:3-4=eaca,i:5-5,i:6-6,i:7-7,p:2-2,d:8-11l,d:12-15l,d:16-16,d:17-17"
    private val NEW_BEACON_LAYOUT = "m:2-3=beef,i:4-4,i:5-5,i:6-6,p:2-2,d:7-8l,d:9-10l,d:11-11"

    public fun startBackgroundScan(centralRangingObserver: Observer<Collection<Beacon>>, centralMonitoringObserver: Observer<Int>) {

        beaconManager.beaconParsers.clear()
        val beaconParserOld = BeaconParser().setBeaconLayout(OLD_BEACON_LAYOUT)
        beaconParserOld.setHardwareAssistManufacturerCodes(MANUFACTURER_CODE)
        val beaconParserNew = BeaconParser().setBeaconLayout(NEW_BEACON_LAYOUT)
        beaconParserNew.setHardwareAssistManufacturerCodes(MANUFACTURER_CODE)
        beaconManager.beaconParsers.add(beaconParserOld)   // has to contain 3 ids otherwise an exception is thrown
        beaconManager.beaconParsers.add(beaconParserNew)

        // By default, the library will scan in the background every 5 minutes on Android 4-7,
        // which will be limited to scan jobs scheduled every ~15 minutes on Android 8+
        // If you want more frequent scanning (requires a foreground service on Android 8+),
        // configure that here.
        // If you want to continuously range beacons in the background more often than every 15 mintues,
        // you can use the library's built-in foreground service to unlock this behavior on Android
        // 8+.   the method below shows how you set that up.
        //setupForegroundService()
        beaconManager.setEnableScheduledScanJobs(true)
        //beaconManager.setBackgroundScanPeriod(1100)
        //beaconManager.setBackgroundBetweenScanPeriod(0)

        // Ranging callbacks will drop out if no beacons are detected
        // Monitoring callbacks will be delayed by up to 25 minutes on region exit
        beaconManager.setIntentScanningStrategyEnabled(true)

        // The code below will start "monitoring" for beacons matching the region definition below
        // the region definition is a wildcard that matches all beacons regardless of identifiers.
        // if you only want to detect beacons with a specific UUID, change the id1 paremeter to
        // a UUID like Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6")
        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)
        // These two lines set up a Live Data observer so this Activity can get beacon data from the Application class
        val regionViewModel = beaconManager.getRegionViewModel(region)
        // observer will be called each time the monitored regionState changes (inside vs. outside region)
        regionViewModel.regionState.observeForever( centralMonitoringObserver)
        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
        regionViewModel.rangedBeacons.observeForever( centralRangingObserver)
    }

    public fun stopForegroundStartBackgroundScan() {
        beaconManager.stopRangingBeacons(region)
        beaconManager.stopMonitoring(region)
        beaconManager.setEnableScheduledScanJobs(true)
        beaconManager.setIntentScanningStrategyEnabled(true)
        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)
    }

    public fun stopBackgroundStartForegroundScan() {
        beaconManager.stopRangingBeacons(region)
        beaconManager.stopMonitoring(region)
        beaconManager.setIntentScanningStrategyEnabled(false)
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundScanPeriod(1100);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)
    }

    fun stop() {
        beaconManager.stopRangingBeacons(region)
        beaconManager.stopMonitoring(region)
    }

    fun isBleEnabled(): Boolean {
        return try {
            beaconManager.checkAvailability()
        }
        catch (e: BleNotAvailableException) {
            false
        }
    }

}