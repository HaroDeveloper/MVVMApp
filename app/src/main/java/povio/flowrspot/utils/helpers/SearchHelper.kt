package povio.flowrspot.utils.helpers

import povio.flowrspot.data.model.Flower

class SearchHelper {
    fun searchFlowers(key: String, list: MutableList<Flower>): List<Flower> {
        return list.filter { it.name.contains(key, true) || it.latinName.contains(key, true)}
    }
}