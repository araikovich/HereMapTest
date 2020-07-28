package araikovich.inc.hereapptest.di

import araikovich.inc.hereapptest.domain.GetCurrentUserLocationUseCase
import araikovich.inc.hereapptest.domain.GetLocationsByRequestUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetCurrentUserLocationUseCase(get()) }
    factory { GetLocationsByRequestUseCase(get()) }
}