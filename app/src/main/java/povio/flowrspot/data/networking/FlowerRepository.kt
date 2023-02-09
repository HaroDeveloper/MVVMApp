package povio.flowrspot.data.networking

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import povio.flowrspot.data.model.FlowerResponse
import povio.flowrspot.ui.home.State
import povio.flowrspot.utils.getResult
import povio.flowrspot.utils.safeApiCall

class FlowerRepository(private val flowerApi: FlowerApi) {
    val flowersDataResponse = MutableSharedFlow<FlowerResponse?>()
    val errorState = MutableSharedFlow<State>()

    suspend fun getFlowers(page: Int) {
        safeApiCall(Dispatchers.IO) {
            flowerApi.getFlowers(page)
        }.getResult(
            success = {
                flowersDataResponse.emit(it)
            },
            networkError = {
                errorState.emit(State.FAILURE_NETWORK)
                Log.d(NETWORK_ERROR, NETWORK_ERROR)
            },
            genericError = { code, message ->
                errorState.emit(State.FAILURE_GENERIC)
                Log.d(GENERIC_ERROR_MESSAGE, message.toString())
                Log.d(GENERIC_ERROR_CODE, code.toString())
            }

        )
    }

    companion object {
        const val GENERIC_ERROR_MESSAGE = "Generic_error_message"
        const val GENERIC_ERROR_CODE = "Generic_error_code"
        const val NETWORK_ERROR = "Network_error"
    }
}