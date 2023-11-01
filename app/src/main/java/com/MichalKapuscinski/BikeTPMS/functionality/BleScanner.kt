package com.MichalKapuscinski.BikeTPMS.functionality

import androidx.lifecycle.Observer
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BleNotAvailableException
import org.altbeacon.beacon.Region

class BleScanner(
    private val beaconManager: BeaconManager,
    private val region: Region
    ) {

    private var isScanning = false
    private val MANUFACTURER_CODE = intArrayOf(0x0100)
    private val OLD_BEACON_LAYOUT = "m:3-4=eaca,i:5-5,i:6-6,i:7-7,p:2-2,d:8-11l,d:12-15l,d:16-16,d:17-17"
    private val NEW_BEACON_LAYOUT = "m:2-3=beef,i:4-4,i:5-5,i:6-6,p:2-2,d:7-8l,d:9-10l,d:11-11"

    init {
        beaconManager.beaconParsers.clear()
        val beaconParserOld = BeaconParser().setBeaconLayout(OLD_BEACON_LAYOUT)
        beaconParserOld.setHardwareAssistManufacturerCodes(MANUFACTURER_CODE)
        val beaconParserNew = BeaconParser().setBeaconLayout(NEW_BEACON_LAYOUT)
        beaconParserNew.setHardwareAssistManufacturerCodes(MANUFACTURER_CODE)
        beaconManager.beaconParsers.add(beaconParserOld)   // has to contain 3 ids otherwise an exception is thrown
        beaconManager.beaconParsers.add(beaconParserNew)
    }

    fun registerObservers(centralRangingObserver: Observer<Collection<Beacon>>, centralMonitoringObserver: Observer<Int>) {
        // These two lines set up a Live Data observer so this Activity can get beacon data from the Application class
        val regionViewModel = beaconManager.getRegionViewModel(region)
        // observer will be called each time the monitored regionState changes (inside vs. outside region)
        regionViewModel.regionState.observeForever(centralMonitoringObserver)
        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
        regionViewModel.rangedBeacons.observeForever(centralRangingObserver)
    }

    fun startBackgroundScan() {
        if (isScanning) {
            stop()
        } else {
            isScanning = true
        }
        beaconManager.setEnableScheduledScanJobs(true)
        beaconManager.setIntentScanningStrategyEnabled(true)

        // The code below will start "monitoring" for beacons matching the region definition below
        // the region definition is a wildcard that matches all beacons regardless of identifiers.
        // if you only want to detect beacons with a specific UUID, change the id1 paremeter to
        // a UUID like Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6")
        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)
    }


    fun startForegroundScan() {
        if (isScanning) {
            stop()
            beaconManager.setIntentScanningStrategyEnabled(false)
            beaconManager.setEnableScheduledScanJobs(false)
        } else {
            isScanning = true
        }
        beaconManager.backgroundScanPeriod = 1100
        beaconManager.backgroundBetweenScanPeriod = 0
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