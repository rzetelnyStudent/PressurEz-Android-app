package com.MichalKapuscinski.BikeTPMS.scanner

import androidx.lifecycle.Observer
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region

class BleScanner(private val _beaconManager: BeaconManager, private val _region: Region) {

    private val beaconManager = _beaconManager

    private val region = _region

    private val MANUFACTURER_CODE = 0x1000

    private val BEACON_LAYOUT = "m:3-4=eaca,i:5-5,i:6-6,i:7-7,p:2-2,d:8-11,d:12-15,d:16-16,d:17-17"

    public fun initialize(centralRangingObserver: Observer<Collection<Beacon>>, centralMonitoringObserver: Observer<Int>) {

        beaconManager.beaconParsers.clear()
        val beaconParser = BeaconParser().setBeaconLayout(BEACON_LAYOUT)
        beaconParser.setHardwareAssistManufacturerCodes(intArrayOf(MANUFACTURER_CODE))
        beaconManager.beaconParsers.add(beaconParser)   // has to contain 3 ids otherwise an exception is thrown
        val regionViewModel = beaconManager.getRegionViewModel(region)
        regionViewModel.regionState.observeForever(centralMonitoringObserver)
        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
        regionViewModel.rangedBeacons.observeForever(centralRangingObserver)
    }

    public fun startBackgroundScan() {
        beaconManager.stopRangingBeacons(region)
        beaconManager.stopMonitoring(region)
        beaconManager.setIntentScanningStrategyEnabled(true)
        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)
    }

    public fun startForegroundScan() {
        beaconManager.stopRangingBeacons(region)
        beaconManager.stopMonitoring(region)
        beaconManager.setIntentScanningStrategyEnabled(false)
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundScanPeriod(1100);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)
    }

}