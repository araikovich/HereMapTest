package araikovich.inc.hereapptest.domain

import araikovich.inc.hereapptest.data.CoordinatesByRequestProvider
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.search.Place

class GetLocationsByRequestUseCase(private val coordinatesByRequestProvider: CoordinatesByRequestProvider) {

    suspend fun execute(
        request: String,
        centerCoordinates: GeoCoordinates,
        locationsCallBack: (List<Place>) -> (Unit)
    ) {
        coordinatesByRequestProvider.getCoordinates(request, centerCoordinates, locationsCallBack)
    }
}