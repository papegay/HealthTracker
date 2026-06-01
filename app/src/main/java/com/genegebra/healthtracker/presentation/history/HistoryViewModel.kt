package com.genegebra.healthtracker.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genegebra.healthtracker.domain.model.HealthEntry
import com.genegebra.healthtracker.domain.model.User
import com.genegebra.healthtracker.domain.repository.AuthRepository
import com.genegebra.healthtracker.domain.usecase.GetEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class HistoryFilter(
    val from: Date? = null,
    val to: Date? = null
)

data class HistoryUiState(
    val isLoading: Boolean = true,
    val currentUser: User? = null,
    val entries: List<HealthEntry> = emptyList(),
    val filter: HistoryFilter = HistoryFilter(),
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getEntriesUseCase: GetEntriesUseCase
) : ViewModel() {

    private val _filter = MutableStateFlow(HistoryFilter())
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val user = authRepository.currentUser.filterNotNull().first()
            _uiState.update { it.copy(currentUser = user) }

            _filter
                .flatMapLatest { filter ->
                    getEntriesUseCase(
                        userId = user.uid,
                        isAdmin = user.isAdmin,
                        from = filter.from,
                        to = filter.to
                    )
                }
                .catch { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
                .collect { entries ->
                    _uiState.update { it.copy(isLoading = false, entries = entries, filter = _filter.value) }
                }
        }
    }

    fun setFilter(from: Date?, to: Date?) {
        _filter.value = HistoryFilter(from = from, to = to)
    }

    fun clearFilter() {
        _filter.value = HistoryFilter()
    }
}
