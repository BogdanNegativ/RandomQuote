package com.example.randomquote

data class QuoteResponse(
    val statusCode: Int,
    val message: String,
    val data: List<Quote>
)
