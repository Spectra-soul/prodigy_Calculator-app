package com.calc.calculatorapp

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val LocalTheme = compositionLocalOf<ThemeCalc> { ThemeCalc() }

class ThemeCalc(): ViewModel() {

    private val _darkMode:MutableStateFlow<Boolean> = MutableStateFlow(false)
    val darkMode = _darkMode.asStateFlow()

    fun toggleTheme(){
        _darkMode.update { !it }
    }
}

