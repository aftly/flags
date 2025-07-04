package dev.aftly.flags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.data.datastore.UserPreferencesRepository
import dev.aftly.flags.model.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


class MainActivityViewModel(
    userPreferencesRepository: UserPreferencesRepository
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
        AppTheme.BLACK.name -> true
        AppTheme.LIGHT.name -> false
        else -> isSystemInDarkTheme
    }

    fun isBlackTheme() = when (uiState.value.theme) {
        AppTheme.BLACK.name -> true
        AppTheme.SYSTEM_BLACK.name -> true
        else -> false
    }

    fun initSystemBarsIsLight(isSystemInDarkTheme: Boolean) =
        when (uiState.value.theme to isSystemInDarkTheme) {
            AppTheme.LIGHT.name to true -> true
            AppTheme.DARK.name to false -> false
            AppTheme.BLACK.name to false -> false
            else -> null
        }
}