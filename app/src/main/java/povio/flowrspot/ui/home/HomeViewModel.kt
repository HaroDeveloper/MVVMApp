package povio.flowrspot.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import povio.flowrspot.data.model.Flower
import povio.flowrspot.data.networking.FlowerRepository
import povio.flowrspot.utils.helpers.SearchHelper

class HomeViewModel(
    private val flowerRepository: FlowerRepository,
    private val searchHelper: SearchHelper
) : ViewModel() {

    private var allFlowers: MutableList<Flower> = mutableListOf()
    val flowers: MutableSharedFlow<List<Flower>> = MutableSharedFlow()
    var state = MutableStateFlow(State.LOADING_INIT)
    private var currentPage: Int = 1
    private var nextPage: Int? = null

    init {
        setCollectors()
        viewModelScope.launch {
            flowerRepository.getFlowers(currentPage)
        }
    }

    private fun setCollectors() {
        viewModelScope.launch {
            flowerRepository.errorState.collect { errorState ->
                state.emit(errorState)
            }
        }

        viewModelScope.launch {
            flowerRepository.flowersDataResponse.collect { flowerData ->
                currentPage = flowerData?.meta?.pagination?.currentPage ?: 1
                nextPage = flowerData?.meta?.pagination?.nextPage

                allFlowers.addAll(flowerData?.flowers ?: emptyList())
                flowers.emit(allFlowers)
                state.emit(State.PAGE_LOADED)
                if (nextPage == null)
                    state.emit(State.LAST_PAGE_REACHED)
            }
        }
    }

    fun nextPage() {
        if (nextPage != null && state.value != State.LOADING_NEXT_PAGE) {
            viewModelScope.launch {
                state.emit(State.LOADING_NEXT_PAGE)
                flowerRepository.getFlowers(currentPage + 1)
            }
        }
    }

    fun search(word: String) {
        viewModelScope.launch {
            if (word.isNotEmpty()) state.emit(State.FILTERING_DONE)
            else state.emit(State.FILTER_REMOVED)

            flowers.emit(searchHelper.searchFlowers(word, allFlowers))
        }
    }
}