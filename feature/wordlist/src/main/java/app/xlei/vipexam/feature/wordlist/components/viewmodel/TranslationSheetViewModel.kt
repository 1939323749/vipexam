package app.xlei.vipexam.feature.wordlist.components.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.xlei.vipexam.core.data.paging.MomoLookUpApi
import app.xlei.vipexam.core.data.paging.MomoLookUpRepository
import app.xlei.vipexam.core.network.module.momoLookUp.Phrase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranslationSheetViewModel @Inject constructor(
    private val momoLookUpRepository: MomoLookUpRepository
) : ViewModel() {
    private val _phrases: MutableStateFlow<PagingData<Phrase>> =
        MutableStateFlow(PagingData.empty())
    val phrases = _phrases

    private var searchJob: Job? = null
    suspend fun search(keyword: String) {
        MomoLookUpApi.keyword = keyword
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            momoLookUpRepository.search().distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _phrases.value = it
                }
        }
    }

    fun clean() {
        searchJob?.cancel()
        _phrases.value = PagingData.empty()
    }

    suspend fun refresh(keyword: String) = search(keyword)
}