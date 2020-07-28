package araikovich.inc.hereapptest.di

import araikovich.inc.hereapptest.data.CoordinatesByRequestProvider
import araikovich.inc.hereapptest.data.PlatformPositionProvider
import com.here.sdk.core.LanguageCode
import com.here.sdk.search.SearchEngine
import com.here.sdk.search.SearchOptions
import org.koin.dsl.module

val positionModule = module {
    factory { PlatformPositionProvider(get()) }
    factory { SearchOptions(LanguageCode.EN_US, 3) }
    factory { SearchEngine() }
    factory { CoordinatesByRequestProvider(get(), get()) }
}