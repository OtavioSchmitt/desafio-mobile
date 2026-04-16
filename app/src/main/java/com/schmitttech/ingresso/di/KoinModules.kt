package com.schmitttech.ingresso.di

import com.schmitttech.ingresso.data.remote.api.IngressoApi
import com.schmitttech.ingresso.data.repository.MoviesRepositoryImpl
import com.schmitttech.ingresso.domain.repository.MoviesRepository
import com.schmitttech.ingresso.domain.usecase.GetComingSoonMoviesUseCase
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import com.schmitttech.ingresso.BuildConfig
import com.schmitttech.ingresso.presentation.details.DetailsViewModel
import com.schmitttech.ingresso.presentation.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val networkModule = module {
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    single<Retrofit> {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get<OkHttpClient>())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    single<IngressoApi> { get<Retrofit>().create(IngressoApi::class.java) }
}

val dataModule = module {
    single<MoviesRepository> { MoviesRepositoryImpl(get()) }
}

val domainModule = module {
    single { GetComingSoonMoviesUseCase(get<MoviesRepository>()) }
}

val presentationModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { DetailsViewModel(get()) }
}

val appModule = listOf(networkModule, dataModule, domainModule, presentationModule)
