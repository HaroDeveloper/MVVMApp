package povio.flowrspot.utils

import kotlinx.coroutines.*
import okhttp3.Dispatcher
import povio.flowrspot.data.remote.ResultWrapper
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ResultWrapper.NetworkError

                is HttpException ->
                    ResultWrapper.GenericError(throwable.code(), throwable.localizedMessage)

                else -> ResultWrapper.GenericError(null, null)
            }
        }
    }
}

suspend fun <T> ResultWrapper<T>.getResult(
    success: suspend (value: T) -> Unit,
    genericError: (suspend (code: Int?, message: String?) -> Unit)? = null,
    networkError: (suspend () -> Unit)? = null
) {

    when (this) {
        is ResultWrapper.Success -> {
            success(value)
        }
        is ResultWrapper.GenericError -> {
            genericError?.let { it(code, errorMessage) }
        }
        is ResultWrapper.NetworkError -> {
            networkError?.let { it() }
        }
    }
}