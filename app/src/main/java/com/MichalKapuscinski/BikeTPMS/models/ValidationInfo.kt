package com.MichalKapuscinski.BikeTPMS.models

enum class ValidationState {
    UNINITIALIZED,
    CORRECT,
    INVALID
}
class ValidationInfo {
    private var state = ValidationState.UNINITIALIZED

    fun registerCorrect() {
        if (state == ValidationState.UNINITIALIZED) {
            state = ValidationState.CORRECT
        }
    }

    fun registerInvalid() {
        state = ValidationState.INVALID
    }

    fun registerState(registeredState: Boolean) {
        if (registeredState) {
            registerCorrect()
        } else {
            registerInvalid()
        }
    }

    fun getState(): ValidationState {
        return state
    }

    fun isCorrect(): Boolean {
        return (state == ValidationState.CORRECT)
    }
}