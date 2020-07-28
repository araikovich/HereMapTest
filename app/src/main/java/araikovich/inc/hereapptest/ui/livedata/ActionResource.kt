package araikovich.inc.bekind.ui.livedata

import upgames.pokerup.android.presentation.viewmodel.ActionState

data class ActionResource<out T> (
    val state: ActionState,
    val data: T? = null,
    val message: String? = null
)
