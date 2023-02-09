package povio.flowrspot.data.networking

import povio.flowrspot.data.model.FlowerResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FlowerApi {
        @GET("flowers")
        suspend fun getFlowers(@Query("page") page: Int): FlowerResponse
}