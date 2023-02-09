package povio.flowrspot.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import povio.flowrspot.data.model.Flower
import povio.flowrspot.data.networking.FlowerRepository

class HomeViewModel(private val flowerRepository: FlowerRepository) : ViewModel() {

    private var allFlowers: MutableList<Flower> = mutableListOf()
    val flowers: MutableStateFlow<List<Flower>> = MutableStateFlow(emptyList())
    var state = MutableSharedFlow<State>()
    private var currentPage: Int = 1
    private var nextPage: Int? = null

    init {
        setCollectors()
        viewModelScope.launch {
            state.emit(State.LOADING_INIT)
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
                if (nextPage == null) state.emit(State.LAST_PAGE_REACHED)
            }
        }
    }
}