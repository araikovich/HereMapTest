package araikovich.inc.hereapptest.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import araikovich.inc.hereapptest.R
import com.here.sdk.search.Place
import kotlinx.android.synthetic.main.cell_item_find_place.view.*

class StopsAdapter(
    private val context: Context, private val textCallBack: (String) -> Unit,
    private val onLocationSelected: (Place) -> Unit
) :
    RecyclerView.Adapter<StopViewHolder>() {

    val items: MutableList<StopModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        return StopViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cell_item_find_place, parent, false)
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {
        holder.bindView(items[position], textCallBack, onLocationSelected)
    }

    fun updateLocationsForLastStop(input: String, locations: List<Place>) {
        items.lastOrNull()?.also {
            it.currentInput = input
            it.places.clear()
            it.places.addAll(locations)
            notifyItemChanged(items.indexOf(it))
        }
    }

    fun addNewStop(stop: StopModel) {
        items.lastOrNull()?.isCompleted = true
        val indexOfLast = items.lastIndex
        items.add(stop)
        notifyItemChanged(indexOfLast)
        notifyItemInserted(items.lastIndex)
    }
}

class StopViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private var locationsAdapter: LocationsAdapter? = null
    private var textCallBack: ((String) -> Unit)? = null

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            //TODO need timer for correct handle fast typing
            textCallBack?.invoke(s.toString())
            locationsAdapter?.items?.clear()
            locationsAdapter?.notifyDataSetChanged()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    fun bindView(
        model: StopModel,
        textCallBack: (String) -> Unit,
        onLocationSelected: (Place) -> Unit
    ) {
        view.tvStopTitle.text = model.title
        if (model.isCompleted) {
            view.tvLocation.visibility = View.VISIBLE
            view.etLocation.visibility = View.GONE
            view.tvLocation.text = model.geoLocation?.address?.addressText.orEmpty()
        } else {
            this.textCallBack = textCallBack
            view.tvLocation.visibility = View.GONE
            view.etLocation.visibility = View.VISIBLE
            setupEditText(model)
            setupAdapter(model, onLocationSelected)
        }
    }

    private fun setupAdapter(
        model: StopModel,
        onLocationSelected: (Place) -> Unit
    ) {
        if (locationsAdapter == null) {
            locationsAdapter = LocationsAdapter(view.context) {
                view.tvLocation.visibility = View.VISIBLE
                view.etLocation.visibility = View.GONE
                locationsAdapter?.items?.clear()
                locationsAdapter?.notifyDataSetChanged()
                model.places.clear()
                model.geoLocation = it
                view.tvLocation.text = it.address.addressText
                model.isCompleted = true
                onLocationSelected.invoke(it)
            }
        }
        view.rvSearchedItems.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        view.rvSearchedItems.adapter = locationsAdapter
        locationsAdapter?.updateItems(model.places)
    }

    private fun setupEditText(model: StopModel) {
        view.etLocation.doOnDetach {
            view.etLocation.removeTextChangedListener(textWatcher)
        }
        view.etLocation.doOnAttach {
            view.etLocation.addTextChangedListener(textWatcher)
        }
        view.etLocation.setText(model.currentInput)
        view.etLocation.setSelection(model.currentInput.length)
    }
}