package araikovich.inc.hereapptest.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import araikovich.inc.hereapptest.R
import araikovich.inc.hereapptest.ui.utils.dpToPx
import araikovich.inc.hereapptest.ui.utils.isPermissionGranted
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.here.sdk.core.*
import com.here.sdk.mapview.*
import com.here.sdk.mapview.MapCamera.OrientationUpdate
import com.here.sdk.routing.CarOptions
import com.here.sdk.routing.RoutingEngine
import com.here.sdk.routing.Waypoint
import com.here.sdk.search.Place
import com.innfinity.permissionflow.lib.permissionFlow
import com.innfinity.permissionflow.lib.withActivity
import com.innfinity.permissionflow.lib.withPermissions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_find_location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import upgames.pokerup.android.presentation.viewmodel.ActionState


class MainActivity : FragmentActivity() {

    private val viewModel: MainViewModel by viewModel()
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var stopsAdapter: StopsAdapter? = null
    private val selectedLocations: MutableList<GeoCoordinates> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView.onCreate(savedInstanceState)
        observeCurrentCoordinates()
        observeSearchPlaces()
        setupListeners()
        getUserLocation()
        setupFindLocationBottomSheet()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    private fun observeCurrentCoordinates() {
        viewModel.currentUserLocation.observe(this, Observer {
            when (it.state) {
                ActionState.SUCCESS -> {
                    it.data?.also { geoCoordinates -> setupCurrentUserLocationOnMap(geoCoordinates) }
                }
            }
        })
    }

    private fun observeSearchPlaces() {
        viewModel.locationsByUserGeoCoordinates.observe(this, Observer {
            when (it.state) {
                ActionState.SUCCESS -> {
                    it.data?.also { date -> providePlacesToSearchDialog(date.input, date.places) }
                }
            }
        })
    }

    private fun getUserLocation() {
        if (isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION) && isPermissionGranted(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            viewModel.findAndLookAtCurrentUserLocation()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                permissionFlow {
                    withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    withActivity(this@MainActivity)
                    request().collect { granted ->
                        viewModel.findAndLookAtCurrentUserLocation()
                    }
                }
            }
        }
    }

    private fun setupCurrentUserLocationOnMap(location: GeoCoordinates) {
        icSearch.visibility = View.VISIBLE
        mapView.mapScene.loadScene(
            MapScheme.NORMAL_DAY
        ) { mapError ->
            if (mapError == null) {
                val distanceInMeters = 1000 * 5.toDouble()
                mapView.camera.lookAt(
                    location,
                    distanceInMeters
                )
                val mapImage =
                    MapImageFactory.fromResource(resources, R.drawable.maps_and_flags)
                val mapMarker = MapMarker(location, mapImage, Anchor2D(0.5F, 1f))

                mapView.mapScene.addMapMarker(mapMarker)

            } else {
                Log.d(
                    "TAG",
                    "Loading map failed: mapError: " + mapError.name
                )
            }
        }
    }

    private fun providePlacesToSearchDialog(input: String, places: List<Place>) {
        stopsAdapter?.updateLocationsForLastStop(input, places)
    }

    private fun setupListeners() {
        icSearch.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
        btnDrawWay.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            drawWay()
        }
        btnAddStop.setOnClickListener {
            addStop()
        }
    }

    private fun setupFindLocationBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(parent_container)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        stopsAdapter = StopsAdapter(this,
            textCallBack = {
                viewModel.findLocationsByUserRequest(it)
            },
            onLocationSelected = {
                selectedLocations.add(it.coordinates)
            })
        rvStops.adapter = stopsAdapter
        rvStops.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun drawWay() {
        viewModel.currentUserLocation.value?.data?.also { currentUserLocation ->
            val listWayPoints = mutableListOf<Waypoint>()
            listWayPoints.add(Waypoint(currentUserLocation))
            selectedLocations.forEach { selectedLocations ->
                listWayPoints.add(Waypoint(selectedLocations))
                drawMapCircle(selectedLocations)
            }
            RoutingEngine().calculateRoute(
                listWayPoints, CarOptions()
            ) { _, routes ->
                routes?.firstOrNull()?.also {
                    val routeGeoPolyline = GeoPolyline(it.polyline)
                    val routMapPolyLine =
                        MapPolyline(
                            routeGeoPolyline,
                            4.dpToPx().toDouble(),
                            Color(109, 130, 212)
                        )
                    mapView.mapScene.addMapPolyline(routMapPolyLine)
                    val routeGeoBox: GeoBox = it.boundingBox
                    mapView.camera.lookAt(routeGeoBox, OrientationUpdate())

                }
            }
        }
    }

    private fun addStop() {
        stopsAdapter?.items?.lastOrNull()?.also {
            if (!it.isCompleted) {
                Toast.makeText(this, "Complete input current stop", Toast.LENGTH_SHORT).show()
                return
            }
        }
        stopsAdapter?.addNewStop(
            StopModel(
                "Stop ${(stopsAdapter?.items?.size ?: 0) + 1}",
                "",
                false,
                null,
                mutableListOf()
            )
        )
    }

    private fun drawMapCircle(coordinate: GeoCoordinates) {
        val geoPolygon = GeoPolygon(GeoCircle(coordinate, 30f))
        val fillColor = Color(109, 130, 212)
        val mapPolygon = MapPolygon(geoPolygon, fillColor)
        mapView.mapScene.addMapPolygon(mapPolygon)
    }
}