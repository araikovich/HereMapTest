package araikovich.inc.bekind.ui.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import upgames.pokerup.android.presentation.viewmodel.ActionState

fun <T> MutableLiveData<T>.toLiveData() = this as LiveData<T>

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, observer: Observer<T>) {
    liveData.observe(this, observer)
}

fun <T> MutableLiveData<ActionResource<T>>.setSuccess(data: T?) =
    postValue(ActionResource(ActionState.SUCCESS, data))

fun <T> MutableLiveData<ActionResource<T>>.setLoading() =
    postValue(ActionResource(ActionState.LOADING, value?.data))

fun <T> MutableLiveData<ActionResource<T>>.setError(message: String? = null) =
    postValue(ActionResource(ActionState.ERROR, value?.data, message))
