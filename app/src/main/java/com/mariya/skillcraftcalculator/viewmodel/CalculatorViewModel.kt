package com.mariya.skillcraftcalculator.viewmodel

import androidx.lifecycle.ViewModel
import com.mariya.skillcraftcalculator.domain.CalculatorEngine
import com.mariya.skillcraftcalculator.domain.CalculatorEngine.Action
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(CalculatorEngine.State())
    val state: StateFlow<CalculatorEngine.State> = _state.asStateFlow()

    fun onAction(action: Action) {
        _state.value = CalculatorEngine.reduce(_state.value, action)
    }
}