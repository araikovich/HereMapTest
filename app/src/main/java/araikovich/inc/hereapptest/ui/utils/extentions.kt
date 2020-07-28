package araikovich.inc.hereapptest.ui.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import araikovich.inc.hereapptest.App
import com.here.sdk.core.GeoCoordinates
import java.util.*

fun Location.toHereLocation(): com.here.sdk.core.Location {
    val geoCoordinates = GeoCoordinates(
        latitude,
        longitude,
        altitude
    )
    val location = com.here.sdk.core.Location(geoCoordinates, Date())
    location.bearingInDegrees = bearing.toDouble()
    location.speedInMetersPerSecond = speed.toDouble()
    location.horizontalAccuracyInMeters = accuracy.toDouble()

    return location
}

fun Activity.isPermissionGranted(permission: String): Boolean =
    checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

fun Int.dpToPx(): Int = (this * App.instance.resources.displayMetrics.density).toInt()