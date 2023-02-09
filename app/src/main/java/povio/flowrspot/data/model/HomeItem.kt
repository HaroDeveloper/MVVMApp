package povio.flowrspot.data.model

sealed class HomeItem {
    class FlowerItem(val flower: Flower) : HomeItem()
    object LoadingItem : HomeItem()
}