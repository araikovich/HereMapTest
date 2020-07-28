package upgames.pokerup.android.presentation.viewmodel

sealed class ActionState {
    object LOADING : ActionState()
    object SUCCESS : ActionState()
    object ERROR : ActionState()
}
