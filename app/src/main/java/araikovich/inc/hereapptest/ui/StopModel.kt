package araikovich.inc.hereapptest.ui

import com.here.sdk.search.Place

data class StopModel(
    val title: String,
    var currentInput: String,
    var isCompleted: Boolean,
    var geoLocation: Place?,
    val places: MutableList<Place> = mutableListOf()
)