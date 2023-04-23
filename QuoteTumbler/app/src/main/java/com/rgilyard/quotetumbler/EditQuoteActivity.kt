package com.rgilyard.quotetumbler

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.rgilyard.quotetumbler.R
import org.json.JSONArray

class EditQuoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_quote)

        // Get edited text and add button
        val editTextQuote: EditText = findViewById(R.id.editText_quote)
        val buttonAddQuote: Button = findViewById(R.id.button_add_quote)

        // Is editing flag will determine whether we add or just edit a quote
        val isEditing = intent.getBooleanExtra("isEditing", false)
        val quoteIndex = intent.getIntExtra("quote_index", -1)
        val originalQuote = intent.getStringExtra("quote")

        // If quote is being edited, set the text to the quote and change the add button
        if (isEditing && originalQuote != null) {
            editTextQuote.setText(originalQuote)
            buttonAddQuote.setText(R.string.edit_button_description)
        }

        // Set click listener
        buttonAddQuote.setOnClickListener {
            val quote = editTextQuote.text.toString()
            if (quote.isNotEmpty()) {
                if (isEditing && quoteIndex != -1) {
                    updateQuote(this, quoteIndex, quote)
                } else {
                    addQuote(this, quote)
                }
                setResult(Activity.RESULT_OK)
                // Return to the previous activity
                finish()
            }
        }
    }

    private fun updateQuote(context: Context, index: Int, newQuote: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("quotes", Context.MODE_PRIVATE)
        val quotesJsonArray = JSONArray(sharedPreferences.getString("quotes_list", "[]"))
        quotesJsonArray.put(index, newQuote)
        sharedPreferences.edit().putString("quotes_list", quotesJsonArray.toString()).apply()
    }

    private fun addQuote(context: Context, quote: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("quotes", Context.MODE_PRIVATE)
        val quotesJsonArray = JSONArray(sharedPreferences.getString("quotes_list", "[]"))
        quotesJsonArray.put(quote)
        sharedPreferences.edit().putString("quotes_list", quotesJsonArray.toString()).apply()
    }
}