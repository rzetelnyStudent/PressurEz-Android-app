package com.MichalKapuscinski.BikeTPMS

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.MichalKapuscinski.BikeTPMS.databinding.FragmentAddBikeBinding
import com.MichalKapuscinski.BikeTPMS.models.Bike
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
        binding.closeButton.setOnClickListener {view ->
            showDiscardChangesDialog(view.context)
        }
        if (myViewModel.bikeAddedOrEdited.value == null) {
            clearInputs()     // not necessary
        } else {
            populateInputs(myViewModel.bikeAddedOrEdited.value)
        }
        enableInputValidation()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        val bike = validateAllInputs()
        if (bike != null) {
            clearInputs()
            myViewModel.bikeAddedOrEdited.value = bike
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

    private fun populateInputs(bike: Bike?) {
        binding.bikeNameField.setText("aaaaa")
        binding.sensorFrontIdField.setText("aaaa")
        binding.sensorRearIdField.setText("aaaa")
        binding.sensorFrontLowPressureField.setText("1,0")
        binding.sensorRearLowPressureField.setText("1,0")
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

    private fun validateAllInputs(): Bike? {
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
                myViewModel.bikeAddedOrEdited.value = null
                dismiss()
            }
            .setPositiveButton(resources.getString(R.string.continue_editing)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}