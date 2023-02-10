package povio.flowrspot.utils

import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import povio.flowrspot.data.remote.ResultWrapper
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException



val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun String.prefixHttp(): String {
    return "http:$this"
}

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

inline fun EditText.onTextChanged(crossinline textChanged: (text: String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            textChanged(p0.toString())
        }
    })
}