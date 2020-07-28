package araikovich.inc.hereapptest.domain

import android.location.Location
import araikovich.inc.hereapptest.data.PlatformLocationListener
import araikovich.inc.hereapptest.data.PlatformPositionProvider
import araikovich.inc.hereapptest.ui.utils.toHereLocation
import com.here.sdk.core.GeoCoordinates

class GetCurrentUserLocationUseCase(private val platformPositioningProvider: PlatformPositionProvider) {

    suspend fun execute(locationCallBack: (GeoCoordinates) -> Unit) {
        platformPositioningProvider.startLocating(object :
            PlatformLocationListener {

            override fun onLocationUpdated(location: Location) {
                locationCallBack.invoke(location.toHereLocation().coordinates)
            }
        })
    }
}