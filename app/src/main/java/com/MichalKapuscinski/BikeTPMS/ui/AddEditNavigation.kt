package com.MichalKapuscinski.BikeTPMS.ui

//import android.content.Context
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.MichalKapuscinski.BikeTPMS.AddBikeFragment
//import com.MichalKapuscinski.BikeTPMS.MyViewModel
//import com.MichalKapuscinski.BikeTPMS.R
//import com.MichalKapuscinski.BikeTPMS.models.Bike
//import com.google.android.material.dialog.MaterialAlertDialogBuilder
//
//object AddEditNavigation {
//    fun showDialogEditDelete(context: Context, bike: Bike) {
//        MaterialAlertDialogBuilder(context)
//            .setMessage("${context.resources.getString(R.string.what_to_do_with_bike)} ${bike.name}")
//            .setNeutralButton(R.string.cancel) { dialog, _ ->
//                dialog.dismiss()
//            }
//            .setNegativeButton(R.string.delete) { dialogParent, _ ->
//                MaterialAlertDialogBuilder(context)
//                    .setMessage("${context.resources.getString(R.string.delete_confirmation)} ${bike.name}?")
//                    .setNeutralButton(R.string.cancel) { dialog, _ ->
//                        dialog.dismiss()
//                    }
//                    .setPositiveButton(R.string.delete) { dialog, _ ->
//                        // delete Bike!!!!!!!
//                        dialog.dismiss()
//                        dialogParent.dismiss()
//                        //Toast
//                    }
//                    .show()
//            }
//            .setPositiveButton(R.string.edit) { dialog, _ ->
//                dialog.dismiss()
//                launchAddEditBike(bike)
//            }
//            .show()
//    }
//
//    private fun launchAddEditBike(context: Context, bike: Bike?) {
//        taskViewModel = ViewModelProvider(context).get(MyViewModel::class.java)
//        taskViewModel.bikeAddedOrEdited.value = bike
//        val addBikeFragment = AddBikeFragment()
//        addBikeFragment.isCancelable = false     // temporarily
//        addBikeFragment.show(context.supportFragmentManager, "newTaskTag")
//    }
//
//}