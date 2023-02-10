package povio.flowrspot.di

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import povio.flowrspot.data.networking.FlowerApi
import povio.flowrspot.data.networking.FlowerRepository
import povio.flowrspot.ui.home.HomeViewModel
import povio.flowrspot.utils.Constants
import povio.flowrspot.utils.helpers.SearchHelper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val retrofitModule = module {
    fun provideGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create()
    }

    fun provideHttpClient(): OkHttpClient {
        val httpBuilder = OkHttpClient.Builder()
        httpBuilder
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)

        return httpBuilder.build()
    }

    fun provideRetrofit(factory: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(factory))
            .client(client)
            .build()
    }

    fun provideFlowrSpotApi(retrofit: Retrofit): FlowerApi =
        retrofit.create(FlowerApi::class.java)

    fun provideFlowrSpotRepository(flowrSpotApi: FlowerApi): FlowerRepository {
        return FlowerRepository(flowrSpotApi)
    }

    single { provideGson() }
    single { provideHttpClient() }
    single { provideRetrofit(get(), get()) }
    single { provideFlowrSpotApi(get()) }
    single { provideFlowrSpotRepository(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
}

val searchModule = module {
    single { SearchHelper() }
}