package com.MichalKapuscinski.BikeTPMS

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.MichalKapuscinski.BikeTPMS.databinding.FragmentAddBikeBinding
import com.MichalKapuscinski.BikeTPMS.models.Action
import com.MichalKapuscinski.BikeTPMS.models.Bike
import com.MichalKapuscinski.BikeTPMS.models.NavigationInfo
import com.MichalKapuscinski.BikeTPMS.ui.formatId
import com.MichalKapuscinski.BikeTPMS.ui.formatLowPressure
import com.MichalKapuscinski.BikeTPMS.ui.validateBikeName
import com.MichalKapuscinski.BikeTPMS.ui.validateLowPressureThreshold
import com.MichalKapuscinski.BikeTPMS.ui.validateSensorId
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddBikeFragment : BottomSheetDialogFragment()
{
    private lateinit var binding: FragmentAddBikeBinding
    private lateinit var myViewModel: MyViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        myViewModel = ViewModelProvider(activity)[MyViewModel::class.java]
        binding.saveButton.setOnClickListener {
            saveAction()
        }
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

//        dialog?.window?.decorView.system(
//            view.system_ui_flag_layout_fullscreen
//                    or view.system_ui_flag_layout_stable
//                    or view.system_ui_flag_hide_navigation
//                    or view.system_ui_flag_fullscreen
//                    or view.system_ui_flag_immersive_sticky
//                    or view.system_ui_flag_layout_hide_navigation
//        )

        binding.closeButton.setOnClickListener {view ->
            showDiscardChangesDialog(view.context)
        }
        val bike = myViewModel.navInfo.value?.bikeAddedOrEdited
        if (bike == null) {
            clearInputs()     // not necessary
        } else {
            populateInputs(bike)
        }
        enableInputValidation()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddBikeBinding.inflate(inflater,container,false)
        return binding.root
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return object: Dialog (requireActivity().
//        OnBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                // handle logic
////                if (shouldNotInvokeAgain) {
////                    this.isEnabled = false
////                }
//            }, theme)
//        }
//    }


//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//        //Code here
//    }


    private fun saveAction()
    {
        // check all user inputs once again
        val bike = validateAllInputs(myViewModel.navInfo.value?.bikeAddedOrEdited)
        if (bike != null) {
            clearInputs()
            myViewModel.navInfo.value = NavigationInfo(bike, Action.ADDED_OR_EDITED)
            dismiss()
        }
    }

    private fun clearInputs() {
        binding.bikeNameField.setText("")
        binding.sensorFrontIdField.setText("")
        binding.sensorRearIdField.setText("")
        binding.sensorFrontLowPressureField.setText("")
        binding.sensorRearLowPressureField.setText("")
    }

    private fun populateInputs(bike: Bike) {
        binding.bikeNameField.setText(bike.name)
        binding.sensorFrontIdField.setText(formatId(bike.sensorFront.id))
        binding.sensorRearIdField.setText(formatId(bike.sensorRear.id))
        binding.sensorFrontLowPressureField.setText(formatLowPressure(bike.sensorFront.lowPressureTh))
        binding.sensorRearLowPressureField.setText(formatLowPressure(bike.sensorRear.lowPressureTh))
    }

    private fun enableInputValidation() {
        binding.bikeNameFieldLayout.editText?.doOnTextChanged { inputText, _, _, _ ->
            binding.bikeNameFieldLayout.error = validateBikeName(inputText.toString(), resources).first
        }
        binding.sensorFrontIdFieldLayout.editText?.doOnTextChanged { inputText, _, _, _ ->
            binding.sensorFrontIdFieldLayout.error = validateSensorId(inputText.toString(), resources).first
        }
        binding.sensorRearIdFieldLayout.editText?.doOnTextChanged { inputText, _, _, _ ->
            binding.sensorRearIdFieldLayout.error = validateSensorId(inputText.toString(), resources).first
        }
        binding.sensorFrontLowPressureFieldLayout.editText?.doOnTextChanged { inputText, _, _, _ ->
            binding.sensorFrontLowPressureFieldLayout.error = validateLowPressureThreshold(inputText.toString(), resources).first
        }
        binding.sensorRearLowPressureFieldLayout.editText?.doOnTextChanged { inputText, _, _, _ ->
            binding.sensorRearLowPressureFieldLayout.error = validateLowPressureThreshold(inputText.toString(), resources).first
        }
    }

    private fun validateAllInputs(editedBike: Bike?): Bike? {
        val bikeNameErrors = validateBikeName(binding.bikeNameField.text.toString(), resources)
        val sensorFrontIdErrors = validateSensorId(binding.sensorFrontIdField.text.toString(), resources)
        val sensorRearIdErrors = validateSensorId(binding.sensorRearIdField.text.toString(), resources)
        val sensorFrontLowErrors = validateLowPressureThreshold(binding.sensorFrontLowPressureField.text.toString(), resources)
        val sensorRearLowErrors = validateLowPressureThreshold(binding.sensorRearLowPressureField.text.toString(), resources)
        binding.bikeNameFieldLayout.error = bikeNameErrors.first
        binding.sensorFrontIdFieldLayout.error = sensorFrontIdErrors.first
        binding.sensorRearIdFieldLayout.error = sensorRearIdErrors.first
        binding.sensorFrontLowPressureFieldLayout.error = sensorFrontLowErrors.first
        binding.sensorRearLowPressureFieldLayout.error = sensorRearLowErrors.first

        var bike: Bike? = null
        if (bikeNameErrors.first == null     // if no errors where detected
            && sensorFrontIdErrors.first == null
            && sensorRearIdErrors.first == null
            && sensorFrontLowErrors.first == null
            && sensorRearLowErrors.first == null) {
            bike = Bike(
                editedBike?.id ?: 0,
                bikeNameErrors.second,
                R.drawable.ic_bike,
                sensorFrontIdErrors.second,
                sensorRearIdErrors.second,
                sensorFrontLowErrors.second,
                sensorRearLowErrors.second)
        }
        return bike
    }

    private fun showDiscardChangesDialog(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setMessage(resources.getString(R.string.discard_changes_message))
            .setNegativeButton(resources.getString(R.string.discard)) { _, _ ->
                myViewModel.navInfo.value = NavigationInfo(null, Action.NOTHING)
                dismiss()
            }
            .setPositiveButton(resources.getString(R.string.continue_editing)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}