package com.example.randomquote

import retrofit2.Response
import retrofit2.http.GET

interface QuoteApiService {
    @GET("/api/v3/quotes/random")
    suspend fun getRandomQuote(): Response<Quote>
}