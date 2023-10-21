package com.MichalKapuscinski.BikeTPMS

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.MichalKapuscinski.BikeTPMS.beacon.permissions.BeaconScanPermissionsActivity
import com.MichalKapuscinski.BikeTPMS.databinding.ActivityMainBinding
import com.MichalKapuscinski.BikeTPMS.functionality.CoreFunctionality
import com.MichalKapuscinski.BikeTPMS.models.Action
import com.MichalKapuscinski.BikeTPMS.models.Bike
import com.MichalKapuscinski.BikeTPMS.models.NavigationInfo
import com.MichalKapuscinski.BikeTPMS.ui.CardAdapter
import com.MichalKapuscinski.BikeTPMS.ui.REQUEST_ENABLE_BT
//import com.MichalKapuscinski.BikeTPMS.utility.Navigation
import com.MichalKapuscinski.BikeTPMS.utility.linkToSettingsIfBtOff
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.MonitorNotifier


class MainActivity : AppCompatActivity(), BikeClickListener {

    private lateinit var coreFunctionality: CoreFunctionality
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskViewModel: MyViewModel
    lateinit var myBikeListAdapter: CardAdapter


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MyApplication)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        coreFunctionality = application as CoreFunctionality
        myBikeListAdapter = CardAdapter(coreFunctionality.bikeList, this)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(applicationContext, 1)
            adapter = myBikeListAdapter
        }
        lifecycle.addObserver(coreFunctionality)


        //val settingsIntent = Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)
        //startActivity(settingsIntent)
        // Set up a Live Data observer for beacon data
        val regionViewModel = BeaconManager.getInstanceForApplication(this)
            .getRegionViewModel(coreFunctionality.region)
        // observer will be called each time the monitored regionState changes (inside vs. outside region)
        regionViewModel.regionState.observe(this, monitoringObserver)
        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
        regionViewModel.rangedBeacons.observe(this, rangingObserver)

        linkToSettingsIfBtOff(coreFunctionality.isBleEnabled())

        taskViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        binding.addBikeBtn.setOnClickListener {
            launchAddEditBike(null)
        }

        taskViewModel.navInfo.observe(this){
            val bike = it.bikeAddedOrEdited
            if (bike != null && it.action == Action.ADDED_OR_EDITED) {
                coreFunctionality.addEditBike(bike)
                Toast.makeText(this,
                    "${bike.name} ${resources.getString(R.string.bike_added_toast)}",
                    Toast.LENGTH_SHORT).show()
                myBikeListAdapter = CardAdapter(coreFunctionality.bikeList, this)
                binding.recyclerView.apply {
                    layoutManager = GridLayoutManager(applicationContext, 1)
                    adapter = myBikeListAdapter
                }
            }
            else if (bike != null && it.action == Action.DELETED)
            {
                coreFunctionality.deleteBike(bike)
                Toast.makeText(this,
                    "${bike.name} ${resources.getString(R.string.bike_deleted)}",
                    Toast.LENGTH_SHORT).show()
                myBikeListAdapter = CardAdapter(coreFunctionality.bikeList, this)
                binding.recyclerView.apply {
                    layoutManager = GridLayoutManager(applicationContext, 1)
                    adapter = myBikeListAdapter
                }
            }
        }
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

        if (!BeaconScanPermissionsActivity.allPermissionsGranted(this,true)) {
            val intent = Intent(this, BeaconScanPermissionsActivity::class.java)
            intent.putExtra("backgroundAccessRequested", true)
            startActivity(intent)
        }
    }

    private val monitoringObserver = Observer<Int> { state ->
        if (state == MonitorNotifier.OUTSIDE) {
            for (bike in coreFunctionality.bikeList) {
                bike.sensorFront.setToNullData()
                bike.sensorRear.setToNullData()
            }
            myBikeListAdapter.notifyDataSetChanged()
        }
    }

    private val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        // Log.d(TAG, "Ranged: ${beacons.count()} beacons")
        if (BeaconManager.getInstanceForApplication(this).rangedRegions.isNotEmpty()) {
            for (bike in coreFunctionality.bikeList) {
                for (sensor in beacons) {
                    bike.sensorFront.updateDataIfDetected(sensor)
                    bike.sensorRear.updateDataIfDetected(sensor)
                }
            }
            myBikeListAdapter.notifyDataSetChanged()
        }
    }


    companion object {
        val TAG = "MainActivity"
        val PERMISSION_REQUEST_BACKGROUND_LOCATION = 0
        val PERMISSION_REQUEST_BLUETOOTH_SCAN = 1
        val PERMISSION_REQUEST_BLUETOOTH_CONNECT = 2
        val PERMISSION_REQUEST_FINE_LOCATION = 3
    }

    override fun onClick(bike: Bike) {
        MaterialAlertDialogBuilder(this)
            .setMessage("${resources.getString(R.string.what_to_do_with_bike)} ${bike.name}")
            .setNeutralButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton(R.string.delete) { dialogParent, _ ->
                MaterialAlertDialogBuilder(this)
                    .setMessage("${resources.getString(R.string.delete_confirmation)} ${bike.name}?")
                    .setNeutralButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(R.string.delete) { dialog, _ ->
                        taskViewModel.navInfo.value = NavigationInfo(bike, Action.DELETED)
                        dialog.dismiss()
                        dialogParent.dismiss()
                    }
                    .show()
            }
            .setPositiveButton(R.string.edit) { dialog, _ ->
                dialog.dismiss()
                launchAddEditBike(bike)
            }
            .show()
    }

    private fun launchAddEditBike(bike: Bike?) {
        taskViewModel.navInfo.value = NavigationInfo(bike, Action.NOTHING)
        val addBikeFragment = AddBikeFragment()
        addBikeFragment.isCancelable = false     // temporarily
        addBikeFragment.show(supportFragmentManager, "newTaskTag")
    }


    private fun enableBT() {

    }

}



