package com.example.randomquote

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://quote-garden.onrender.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val quoteApiService = retrofit.create(QuoteApiService::class.java)

    private lateinit var quoteTextView: TextView

    private fun loadRandomQuote() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    quoteApiService.getRandomQuote()
                }

                Log.d("QuoteActivity", "Response: ${response.code()}, ${response.message()}, Body: ${response.body()}")

                if (response.isSuccessful) {
                    val quoteResponse = response.body()

                    if (quoteResponse != null && quoteResponse.data.isNotEmpty()) {
                        val quote = quoteResponse.data[0]
                        quoteTextView.text = "\"${quote.quoteText}\" - ${quote.quoteAuthor}"
                    } else {
                        Toast.makeText(this@MainActivity, "Получены некорректные данные цитаты", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Ошибка загрузки цитаты", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("QuoteActivity", "Error: ${e.message}")
                Toast.makeText(this@MainActivity, "Произошла ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quoteTextView = findViewById(R.id.quoteTextView)
        val refreshButton: Button = findViewById(R.id.refreshButton)

        refreshButton.setOnClickListener {
            loadRandomQuote()
        }

        loadRandomQuote()
    }
}