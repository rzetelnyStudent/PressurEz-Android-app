package com.MichalKapuscinski.BikeTPMS

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.MichalKapuscinski.BikeTPMS.databinding.ActivityMainBinding
import com.MichalKapuscinski.BikeTPMS.functionality.CoreFunctionality
import com.MichalKapuscinski.BikeTPMS.models.Action
import com.MichalKapuscinski.BikeTPMS.models.Bike
import com.MichalKapuscinski.BikeTPMS.models.NavigationInfo
import com.MichalKapuscinski.BikeTPMS.models.ValidationInfo
import com.MichalKapuscinski.BikeTPMS.permissions.PermissionsHelper
import com.MichalKapuscinski.BikeTPMS.ui.CardAdapter
//import com.MichalKapuscinski.BikeTPMS.utility.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.MonitorNotifier


class MainActivity : AppCompatActivity(), BikeClickListener {

    lateinit var coreFunctionality: CoreFunctionality
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskViewModel: MyViewModel
    private lateinit var myBikeListAdapter: CardAdapter
    lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>


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
        requestPermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = ValidationInfo()
            for (permissionResult in permissions.values) {
                allGranted.registerState(permissionResult)
            }
            if (allGranted.isCorrect()) {
                coreFunctionality.startScan()
            }
        }

        // Set up a Live Data observer for beacon data
        val regionViewModel = BeaconManager.getInstanceForApplication(this)
            .getRegionViewModel(coreFunctionality.region)
        regionViewModel.regionState.observe(this, monitoringObserver)
        regionViewModel.rangedBeacons.observe(this, rangingObserver)

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

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        // You MUST make sure the following dynamic permissions are granted by the user to detect beacons
        //    Manifest.permission.BLUETOOTH_SCAN
        //    Manifest.permission.ACCESS_FINE_LOCATION
        //    Manifest.permission.ACCESS_BACKGROUND_LOCATION
        //    Manifest.permission.POST_NOTIFICATIONS
        linkToSettingsIfBtOff(coreFunctionality.isBleEnabled())
        promptConditionallyForPermissions()
        conditionallyLinkToBatterySettings()
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
}



