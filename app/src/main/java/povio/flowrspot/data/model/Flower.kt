package povio.flowrspot.data.model

import com.google.gson.annotations.SerializedName

data class Flower(
    val id: Int,
    val name: String,
    @SerializedName("latin_name") val latinName: String,
    val sightings: Int,
    @SerializedName("profile_picture") val profilePicture: String,
    val favorite: Boolean
)