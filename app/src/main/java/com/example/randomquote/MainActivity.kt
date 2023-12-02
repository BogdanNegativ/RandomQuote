package com.example.randomquote

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                quoteApiService.getRandomQuote()
            }

            if (response.isSuccessful) {
                val quote = response.body()
                quoteTextView.text = "\"${quote?.quoteText}\" - ${quote?.author}"
            } else {
                // Обработка ошибок при запросе цитаты
                Toast.makeText(this@MainActivity, "Ошибка загрузки цитаты", Toast.LENGTH_SHORT).show()
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