package com.dcac.dessertrelease.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dcac.dessertrelease.DessertReleaseApplication
import com.dcac.dessertrelease.R
import com.dcac.dessertrelease.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*
 * View model of Dessert Release components
 */
class DessertReleaseViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DessertReleaseUiState())

    // UI states access for various [DessertReleaseUiState]
    //val uiState: StateFlow<DessertReleaseUiState> = _uiState
    val uiState: StateFlow<DessertReleaseUiState> =
        userPreferencesRepository.isLinearLayout.map { isLinearLayout ->
            DessertReleaseUiState(isLinearLayout)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DessertReleaseUiState()
            )
    /*
     * [selectLayout] change the layout and icons accordingly and
     * save the selection in DataStore through [userPreferencesRepository]
     */
    fun selectLayout(isLinearLayout: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveLayoutPreference(isLinearLayout)
        }
        //_uiState.value = DessertReleaseUiState(isLinearLayout)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DessertReleaseApplication)
                DessertReleaseViewModel(application.userPreferencesRepository)
            }
        }
    }
}

/*
 * Data class containing various UI States for Dessert Release screens
 */
data class DessertReleaseUiState(
    val isLinearLayout: Boolean = true,
    val toggleContentDescription: Int =
        if (isLinearLayout) R.string.grid_layout_toggle else R.string.linear_layout_toggle,
    val toggleIcon: Int =
        if (isLinearLayout) R.drawable.ic_grid_layout else R.drawable.ic_linear_layout
)