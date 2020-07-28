package araikovich.inc.hereapptest.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import araikovich.inc.hereapptest.R
import araikovich.inc.hereapptest.ui.utils.DiffDefaultCallback
import com.here.sdk.search.Place
import kotlinx.android.synthetic.main.cell_item_place.view.*

class LocationsAdapter(private val context: Context, private val onClick: (Place) -> Unit) :
    RecyclerView.Adapter<LocationViewHolder>() {

    val items = mutableListOf<Place>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cell_item_place, parent, false)
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bindView(items[position], onClick)
    }

    fun updateItems(newList: List<Place>) {
        val diffResult = DiffUtil.calculateDiff(DiffDefaultCallback(newList, items))
        items.clear()
        items.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}

class LocationViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bindView(model: Place, onClick: (Place) -> Unit) {
        view.tvPlace.text = model.address.addressText
        view.setOnClickListener {
            onClick.invoke(model)
        }
    }
}