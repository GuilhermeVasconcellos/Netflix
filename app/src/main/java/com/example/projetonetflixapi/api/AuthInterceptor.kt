package com.example.projetonetflixapi.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val requisicaoAtual = chain.request().newBuilder()
       /* // usando api_key
        val urlAtual = chain.request().url()
        val novaUrl = urlAtual.newBuilder()
            .addQueryParameter("api_key", RetrofitService.API_KEY)
            .build()
        val novaRequisicao = requisicaoAtual.url(novaUrl).build()
        */

        // usando bearer token
        val novaRequisicao = requisicaoAtual.addHeader(
            "Authorization",
            "Bearer ${RetrofitService.TOKEN}"
        ).build()

        return chain.proceed(novaRequisicao)
    }

}