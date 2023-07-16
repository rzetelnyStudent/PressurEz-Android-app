package com.MichalKapuscinski.BikeTPMS

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.MichalKapuscinski.BikeTPMS.beacon.permissions.BeaconScanPermissionsActivity
import com.MichalKapuscinski.BikeTPMS.databinding.ActivityMainBinding
import com.MichalKapuscinski.BikeTPMS.models.Bike
import com.MichalKapuscinski.BikeTPMS.models.Sensor
import com.MichalKapuscinski.BikeTPMS.scanner.CoreFunctionality
import com.MichalKapuscinski.BikeTPMS.ui.CardAdapter
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.MonitorNotifier


class MainActivity : AppCompatActivity() {

    private lateinit var coreFunctionality: CoreFunctionality
    var alertDialog: AlertDialog? = null
    private lateinit var binding: ActivityMainBinding
    lateinit var myBikeListAdapter: CardAdapter


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        coreFunctionality = application as CoreFunctionality
        myBikeListAdapter = CardAdapter(coreFunctionality.bikeList)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(applicationContext, 1)
            adapter = myBikeListAdapter
        }
        lifecycle.addObserver(coreFunctionality)
        // Set up a Live Data observer for beacon data
        val regionViewModel = BeaconManager.getInstanceForApplication(this)
            .getRegionViewModel(coreFunctionality.region)
        // observer will be called each time the monitored regionState changes (inside vs. outside region)
        regionViewModel.regionState.observe(this, monitoringObserver)
        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
        regionViewModel.rangedBeacons.observe(this, rangingObserver)

    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        // You MUST make sure the following dynamic permissions are granted by the user to detect beacons
        //
        //    Manifest.permission.BLUETOOTH_SCAN
        //    Manifest.permission.BLUETOOTH_CONNECT
        //    Manifest.permission.ACCESS_FINE_LOCATION
        //    Manifest.permission.ACCESS_BACKGROUND_LOCATION // only needed to detect in background
        //
        // The code needed to get these permissions has become increasingly complex, so it is in
        // its own file so as not to clutter this file focussed on how to use the library.

        if (!BeaconScanPermissionsActivity.allPermissionsGranted(
                this,
                true
            )
        ) {
            val intent = Intent(this, BeaconScanPermissionsActivity::class.java)
            intent.putExtra("backgroundAccessRequested", true)
            startActivity(intent)
        }
    }

    private val monitoringObserver = Observer<Int> { state ->
        var dialogTitle = "Beacons detected"
        var dialogMessage = "didEnterRegionEvent has fired"
        var stateString = "inside"
        if (state == MonitorNotifier.OUTSIDE) {
            dialogTitle = "No beacons detected"
            dialogMessage = "didExitRegionEvent has fired"
            stateString = "outside"
            for (bike in coreFunctionality.bikeList) {
                bike.sensorFront.setToNullData()
                bike.sensorRear.setToNullData()
            }
            myBikeListAdapter.notifyDataSetChanged()
        }
        Log.d(TAG, "monitoring state changed to : $stateString")
        val builder =
            AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.setPositiveButton(android.R.string.ok, null)
        alertDialog?.dismiss()
        alertDialog = builder.create()
        alertDialog?.show()
    }

    val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        // Log.d(TAG, "Ranged: ${beacons.count()} beacons")
        if (BeaconManager.getInstanceForApplication(this).rangedRegions.isNotEmpty()) {
            for (bike in coreFunctionality.bikeList) {
                for (sensor in beacons) {
                    if (bike.sensorFront.equalId(
                            sensor.id1.toInt(),
                            sensor.id2.toInt(),
                            sensor.id3.toInt()
                        )
                    ) {    // exceptions!!!!
                        bike.sensorFront.updateMeasurementFromAdvData(sensor.dataFields)
                    } else {
                        bike.sensorFront.setToNullData()
                    }
                    if (bike.sensorRear.equalId(
                            sensor.id1.toInt(),
                            sensor.id2.toInt(),
                            sensor.id3.toInt()
                        )
                    ) {    // exceptions!!!!
                        bike.sensorRear.updateMeasurementFromAdvData(sensor.dataFields)
                    } else {
                        bike.sensorRear.setToNullData()
                    }
                }
            }
            myBikeListAdapter.notifyDataSetChanged()
        }
    }

//    fun rangingButtonTapped(view: View) {
//        val beaconManager = BeaconManager.getInstanceForApplication(this)
//        if (beaconManager.rangedRegions.size == 0) {
//            beaconManager.startRangingBeacons(beaconReferenceApplication.region)
//            rangingButton.text = "Stop Ranging"
//            beaconCountTextView.text = "Ranging enabled -- awaiting first callback"
//        } else {
//            beaconManager.stopRangingBeacons(beaconReferenceApplication.region)
//            rangingButton.text = "Start Ranging"
//            beaconCountTextView.text = "Ranging disabled -- no beacons detected"
//            beaconListView.adapter =
//                ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("--"))
//        }
//    }
//
//    fun monitoringButtonTapped(view: View) {
//        var dialogTitle = ""
//        var dialogMessage = ""
//        val beaconManager = BeaconManager.getInstanceForApplication(this)
//        if (beaconManager.monitoredRegions.size == 0) {
//            beaconManager.startMonitoring(beaconReferenceApplication.region)
//            dialogTitle = "Beacon monitoring started."
//            dialogMessage =
//                "You will see a dialog if a beacon is detected, and another if beacons then stop being detected."
//            monitoringButton.text = "Stop Monitoring"
//
//        } else {
//            beaconManager.stopMonitoring(beaconReferenceApplication.region)
//            dialogTitle = "Beacon monitoring stopped."
//            dialogMessage = "You will no longer see dialogs when becaons start/stop being detected."
//            monitoringButton.text = "Start Monitoring"
//        }
//        val builder =
//            AlertDialog.Builder(this)
//        builder.setTitle(dialogTitle)
//        builder.setMessage(dialogMessage)
//        builder.setPositiveButton(android.R.string.ok, null)
//        alertDialog?.dismiss()
//        alertDialog = builder.create()
//        alertDialog?.show()
//
//    }


    companion object {
        val TAG = "MainActivity"
        val PERMISSION_REQUEST_BACKGROUND_LOCATION = 0
        val PERMISSION_REQUEST_BLUETOOTH_SCAN = 1
        val PERMISSION_REQUEST_BLUETOOTH_CONNECT = 2
        val PERMISSION_REQUEST_FINE_LOCATION = 3
    }

}



