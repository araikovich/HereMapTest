package araikovich.inc.hereapptest.data

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat


class PlatformPositionProvider(private val context: Context) :
    android.location.LocationListener {

    companion object {
        private const val LOCATION_UPDATE_INTERVAL_IN_MS = 100L
    }

    private var locationManager: LocationManager? = null
    private var platformLocationListener: PlatformLocationListener? = null

    override fun onLocationChanged(location: Location?) {
        location?.also {
            platformLocationListener?.onLocationUpdated(location)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {
        Log.d("TAG", "PlatformPositioningProvider enabled.");
    }

    override fun onProviderDisabled(provider: String?) {
        Log.d("TAG", "PlatformPositioningProvider disabled.");
    }

    fun startLocating(locationCallback: PlatformLocationListener?) {
        stopLocating()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("TAG", "Positioning permissions denied.")
            return
        }
        platformLocationListener = locationCallback!!
        locationManager =
            context.getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER).orFalse() &&
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
        ) {
            locationManager?.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                this,
                null
            )
        } else if (locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER).orFalse()) {
            locationManager?.requestSingleUpdate(
                LocationManager.NETWORK_PROVIDER,
                this,
                null
            )
        } else {
            Log.d("TAG", "Positioning not possible.")
            stopLocating()
        }
    }

    fun stopLocating() {
        if (locationManager == null) {
            return
        }
        locationManager?.removeUpdates(this)
        platformLocationListener = null
    }
}

interface PlatformLocationListener {

    fun onLocationUpdated(location: Location)
}

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}