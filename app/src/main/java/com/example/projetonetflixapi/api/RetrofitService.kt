package com.example.projetonetflixapi.api

import com.example.projetonetflixapi.api.FilmeAPI
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//object RetrofitService { // padrão de projeto SINGLETON
class RetrofitService {
    companion object {
        const val API_KEY = "857432200214020907a007b69c6604ac"
        const val TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4NTc0MzIyMDAyMTQwMjA5MDdhMDA3YjY5YzY2MDRhYyIsInN1YiI6IjYzNDVmM2NiYjNlNjI3MDA4MjgxYmMzOSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.ZMz7YA1YchWIhSTPFDYmL2VdGtw-2BKrv2noHb-FKII"
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val BASE_URL_IMAGE = "https://image.tmdb.org/t/p/"

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(30,TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
//            .connectTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val filmeAPI = retrofit.create(FilmeAPI::class.java)
    }

}