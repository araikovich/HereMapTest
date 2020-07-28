package araikovich.inc.hereapptest.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import araikovich.inc.bekind.ui.base.BaseViewModel
import araikovich.inc.bekind.ui.livedata.ActionResource
import araikovich.inc.bekind.ui.livedata.setSuccess
import araikovich.inc.hereapptest.domain.GetCurrentUserLocationUseCase
import araikovich.inc.hereapptest.domain.GetLocationsByRequestUseCase
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.search.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val getCurrentUserLocationUseCase: GetCurrentUserLocationUseCase,
    private val getLocationsByRequestUseCase: GetLocationsByRequestUseCase
) :
    BaseViewModel() {

    val currentUserLocation = MutableLiveData<ActionResource<GeoCoordinates>>()
    val locationsByUserGeoCoordinates = MutableLiveData<ActionResource<NearestLocationsModel>>()

    fun findAndLookAtCurrentUserLocation() {
        viewModelScope.launch {
            getCurrentUserLocationUseCase.execute { coordinates ->
                viewModelScope.launch(Dispatchers.Main) {
                    currentUserLocation.setSuccess(coordinates)
                }
            }
        }
    }

    fun findLocationsByUserRequest(request: String) {
        viewModelScope.launch {
            currentUserLocation.value?.data?.also { userCoordinates ->
                getLocationsByRequestUseCase.execute(request, userCoordinates) { places ->
                    viewModelScope.launch(Dispatchers.Main) {
                        locationsByUserGeoCoordinates.setSuccess(
                            NearestLocationsModel(
                                request,
                                places
                            )
                        )
                    }
                }
            }
        }
    }
}

data class NearestLocationsModel(
    val input: String,
    val places: List<Place>
)