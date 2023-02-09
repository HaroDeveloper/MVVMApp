package povio.flowrspot.ui.home

enum class State {
    LOADING_INIT,
    LOADING_NEXT_PAGE,
    LAST_PAGE_REACHED,
    PAGE_LOADED,
    FILTERING_DONE,
    FILTER_REMOVED,
    FAILURE_GENERIC,
    FAILURE_NETWORK
}