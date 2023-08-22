package com.example.projetonetflixapi.api

import com.example.projetonetflixapi.model.FilmeDetalhes
import com.example.projetonetflixapi.model.FilmeRecente
import com.example.projetonetflixapi.model.FilmeResposta
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmeAPI {

//    @GET("movie/popular?api_key=${RetrofitService.API_KEY}")
    @GET("movie/popular")
    suspend fun recuperarFilmesPopulares(@Query("page") pagina: Int = 1) : Response<FilmeResposta>

//    @GET("movie/latest?api_key=${RetrofitService.API_KEY}")
    @GET("movie/latest")
    suspend fun recuperarFilmeRecente() : Response<FilmeRecente>

//    @GET("movie/{movie_id}?api_key=${RetrofitService.API_KEY}")
    @GET("movie/{movie_id}")
    suspend fun recuperarFilmeDetalhes(@Path("movie_id") idFilme: Int) : Response<FilmeDetalhes>

    @GET("search/movie")
    suspend fun recuperarFilmePesquisa(
        @Query("query") pesquisa: String
    ) : Response<FilmeResposta>
}