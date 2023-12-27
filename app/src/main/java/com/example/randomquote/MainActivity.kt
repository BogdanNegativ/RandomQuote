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
    private lateinit var quoteButton: Button

    private fun loadRandomQuote() {
        quoteButton.setEnabled(false);
        quoteTextView.text="Цитата загружается..."
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
            quoteButton.setEnabled(true);
        }

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("quoteText", quoteTextView.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedQuote = savedInstanceState.getString("quoteText")
        quoteTextView.text = savedQuote
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quoteTextView = findViewById(R.id.quoteTextView)
        quoteButton=findViewById(R.id.refreshButton)
        quoteButton.setOnClickListener{
            loadRandomQuote()
        }
        if (savedInstanceState != null) {
            val savedQuote = savedInstanceState.getString("quoteText")
            quoteTextView.text = savedQuote
        } else {
            loadRandomQuote()
        }
    }
}