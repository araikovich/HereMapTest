package araikovich.inc.hereapptest.data

import com.here.sdk.core.GeoCoordinates
import com.here.sdk.search.Place
import com.here.sdk.search.SearchEngine
import com.here.sdk.search.SearchOptions
import com.here.sdk.search.TextQuery

class CoordinatesByRequestProvider(
    private val searchOptions: SearchOptions,
    private val searchEngine: SearchEngine
) {

    suspend fun getCoordinates(
        request: String,
        centerCoordinates: GeoCoordinates,
        placesCallBack: (List<Place>) -> Unit
    ) {
        searchEngine.suggest(
            TextQuery(request, centerCoordinates),
            searchOptions
        ) { _, suggestions ->
            val resultList = mutableListOf<Place>()
            suggestions?.forEach {
                it.place?.also { place ->
                    resultList.add(place)
                }
            }
            placesCallBack.invoke(resultList)
        }
    }
}