package dev.aftly.flags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.aftly.flags.data.UserPreferencesRepository
import dev.aftly.flags.model.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainActivityViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> =
        combine(
            userPreferencesRepository.isDynamicColor,
            userPreferencesRepository.theme
        ) { isDynamicColor, theme ->
            MainActivityUiState(isDynamicColor, theme)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = MainActivityUiState()
        )
    
    fun isDarkTheme(isSystemInDarkTheme: Boolean) = when (uiState.value.theme) {
        AppTheme.DARK.name -> true
        AppTheme.LIGHT.name -> false
        else -> isSystemInDarkTheme
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlagsApplication)
                MainActivityViewModel(application.userPreferencesRepository)
            }
        }
    }
}