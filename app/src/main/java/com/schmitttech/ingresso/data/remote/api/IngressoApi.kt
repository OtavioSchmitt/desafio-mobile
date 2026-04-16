package com.schmitttech.ingresso.data.remote.api

import com.schmitttech.ingresso.data.remote.model.MoviesResponse
import retrofit2.http.GET

interface IngressoApi {

    @GET("v0/events/coming-soon/partnership/desafio")
    suspend fun getComingSoonMovies(): MoviesResponse

}
