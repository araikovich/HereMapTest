package araikovich.inc.hereapptest.di

import araikovich.inc.hereapptest.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { MainViewModel(get(), get()) }
}