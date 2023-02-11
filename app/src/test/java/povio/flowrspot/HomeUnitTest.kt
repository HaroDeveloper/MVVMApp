package povio.flowrspot

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import povio.flowrspot.data.model.FlowerResponse
import povio.flowrspot.data.networking.FlowerApi
import povio.flowrspot.di.retrofitModule
import povio.flowrspot.di.searchModule
import povio.flowrspot.utils.helpers.SearchHelper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStreamReader
import java.util.logging.Level
import java.util.logging.Logger

class HomeUnitTest : KoinTest {
    private lateinit var server: MockWebServer
    private val searchHelper: SearchHelper by inject()
    private val gson: Gson by inject()
    private val client = OkHttpClient.Builder().build()
    lateinit var flowerApi: FlowerApi

    init {
        startKoin { modules(listOf(retrofitModule, searchModule)) }
        Logger.getLogger(MockWebServer::class.java.name).level = Level.WARNING
    }

    @Before
    fun initTest() {
        server = MockWebServer()

        val converterFactory = GsonConverterFactory.create(gson)
        flowerApi = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
            .create(FlowerApi::class.java)
    }

    @After
    fun shutdown() {
        server.shutdown()
        stopKoin()
    }

    @Test
    fun mappingTest() {
        runBlocking {
            withContext(Dispatchers.IO) {
                server.apply {
                    enqueue(MockResponse().setBody(readFile("home_flowers_response.json")))
                }

                val response = flowerApi.getFlowers(2)

                assert(response != null)
                assert(response?.flowers != null)
                Assert.assertEquals(response?.flowers?.size, 10)
                Assert.assertEquals(response?.flowers?.get(1)?.id, 38)
                Assert.assertEquals(response?.flowers?.get(1)?.name, "Balloon Flower")
                Assert.assertEquals(response?.flowers?.get(1)?.latinName, "Platycodon grandiflorus")
                Assert.assertEquals(
                    response?.flowers?.get(1)?.profilePicture,
                    "//flowrspot.s3.amazonaws.com/flowers/profile_pictures/000/000/038/medium/pl-image.png?1656446346"
                )
                Assert.assertEquals(response?.flowers?.get(1)?.favorite, false)
            }
        }
    }

    @Test
    fun testListSearch() {
        val flowerList = gson.fromJson(
            readFile("home_flowers_response.json"),
            FlowerResponse::class.java
        ).flowers
        Assert.assertEquals(searchHelper.searchFlowers("go", flowerList.toMutableList()).size, 2)
        Assert.assertEquals(searchHelper.searchFlowers("", flowerList.toMutableList()).size, 10)
        Assert.assertEquals(searchHelper.searchFlowers("Marsh", flowerList.toMutableList()).size, 1)
        Assert.assertEquals(
            searchHelper.searchFlowers(
                "notAvailable",
                flowerList.toMutableList()
            ).size, 0
        )
    }

    private fun readFile(path: String): String {
        val reader = InputStreamReader(this.javaClass.classLoader?.getResourceAsStream(path)!!)
        val content = reader.readText()
        reader.close()
        return content
    }
}