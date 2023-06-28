package com.rgilyard.quotetumbler

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.json.JSONArray
import java.io.BufferedReader
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.rgilyard.quotetumbler.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private var addQuoteActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var quoteDetailActivityLauncher: ActivityResultLauncher<Intent>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind add new quote button
        val fabAddQuote: FloatingActionButton = findViewById(R.id.fab_add_quote)
        fabAddQuote.setOnClickListener {
            addNewQuote()
        }

        // Load the hardcoded quotes from assets, put them in shared preferences
        loadInitialQuotes(this)

        // Populate list from shared preferences
        populateList(this)

        // Updates quote list after new quote is added
        addQuoteActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                updateQuotesList()
            }
        }

        // Updates quote list after quote is edited or deleted
        quoteDetailActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                updateQuotesList()
            }
        }
    }

    private fun addNewQuote() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("quotes", Context.MODE_PRIVATE)
        val quotesJsonArray = JSONArray(sharedPreferences.getString("quotes_list", "[]"))
        if (quotesJsonArray != null && quotesJsonArray.length() >= 100) {
            Toast.makeText(this, "You have reached the 100 quote limit.", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, EditQuoteActivity::class.java)
            addQuoteActivityResultLauncher?.launch(intent)
        }
    }

    private fun updateQuotesList() {
        val quotesLayout = findViewById<LinearLayout>(R.id.dynamicLinearLayout)
        quotesLayout.removeAllViews()
        populateList(this)
    }

    // Populates the xml list
    private fun populateList(context: Context) {
        // Get list layout
        val dynamicLinearLayout = findViewById<LinearLayout>(R.id.dynamicLinearLayout)
        // Load quotes from json
        val quotes = getQuotes()

        for ((index, quote) in quotes.withIndex()) {
            val textView = TextView(this).apply {
                // Eventually set font size based on quote length (and widget size?)
                text = quote
                textSize = 20f
                setPadding(24, 24, 24, 24)
            }

            val cardView = MaterialCardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(32, 4, 32, 12)
                }
                radius = 16.dpToPx().toFloat()
                cardElevation = 4.dpToPx().toFloat()
                addView(textView)
            }

            cardView.setOnClickListener {
                val intent = Intent(this, QuoteDetailActivity::class.java)
                intent.putExtra("quote_index", index) // Pass the index of the selected quote
                intent.putExtra("quote", quote)
                quoteDetailActivityLauncher?.launch(intent)
            }

            dynamicLinearLayout.addView(cardView)
        }
    }

    // Gets quotes from shared preferences
    private fun getQuotes(): List<String> {
        val sharedPreferences: SharedPreferences = getSharedPreferences("quotes", Context.MODE_PRIVATE)
        val quotesJsonArray = JSONArray(sharedPreferences.getString("quotes_list", "[]"))
        val quotesList = mutableListOf<String>()
        for (i in 0 until quotesJsonArray.length()) {
            quotesList.add(quotesJsonArray.getString(i))
        }
        return quotesList
    }

    // Gets quotes from shared preferences or the json quotes list
    private fun loadInitialQuotes(context: Context) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("quotes", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("quotes_list")) {
            val quotes = loadQuotesFromAssets(context)
            val quotesJsonArray = JSONArray(quotes)
            sharedPreferences.edit().putString("quotes_list", quotesJsonArray.toString()).apply()
        }
    }

    // Loads hardcoded quotes from assets
    private fun loadQuotesFromAssets(context: Context): List<String> {
        val inputStream = context.assets.open("quote_1.json")
        val jsonText: String = inputStream.bufferedReader().use(BufferedReader::readText)
        val jsonArray = JSONArray(jsonText)
        val quotesList = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            quotesList.add(jsonArray.getString(i))
        }
        return quotesList
    }

    // This function seems to convert dp to pixels for some reason. Not sure why.
    private fun Int.dpToPx() = (this * resources.displayMetrics.density).toInt()
}