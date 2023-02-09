package povio.flowrspot.data.model

import com.google.gson.annotations.SerializedName

data class Pagination(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("prev_page") val prevPage: Int,
    @SerializedName("next_page") val nextPage: Int?,
    @SerializedName("total_pages") val totalPages: Int
)