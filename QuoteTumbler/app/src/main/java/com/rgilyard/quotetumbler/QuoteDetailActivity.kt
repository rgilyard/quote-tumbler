package com.rgilyard.quotetumbler

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.rgilyard.quotetumbler.R
import org.json.JSONArray

class QuoteDetailActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var editQuoteActivityResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote_detail)

        // Get delete button
        val deleteButton = findViewById<Button>(R.id.delete_button)

        // Get quotes
        sharedPreferences = getSharedPreferences("quotes", Context.MODE_PRIVATE)

        // Get quote info from intent
        val quoteIndex = intent.getIntExtra("quote_index", -1)
        val quote = intent.getStringExtra("quote")
        if (quoteIndex != -1) {
            // If a quote is found, show in text view
            findViewById<TextView>(R.id.quote_text).text = quote
        }

        deleteButton.setOnClickListener {
            if (quoteIndex != -1) {
                val result = deleteQuote(quoteIndex, quote)
                if (result) {
                    setResult(Activity.RESULT_OK)
                } else {
                    setResult(Activity.RESULT_CANCELED)
                }
                finish()
            }
        }

        // Get edit button
        val editButton = findViewById<Button>(R.id.edit_button)

        // Configure the activity result launcher
        editQuoteActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        editButton.setOnClickListener {
            val intent = Intent(this, EditQuoteActivity::class.java)
            intent.putExtra("isEditing", true)
            intent.putExtra("quote_index", quoteIndex)
            intent.putExtra("quote", quote)
            editQuoteActivityResultLauncher?.launch(intent)
        }
    }

    private fun deleteQuote(index: Int, quote: String?): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("quotes", Context.MODE_PRIVATE)
        val quotesJsonArray = JSONArray(sharedPreferences.getString("quotes_list", "[]"))

        if (quote != null && index >= 0 && index < quotesJsonArray.length()) {
            if (quotesJsonArray.getString(index) == quote) {
                quotesJsonArray.remove(index)
                sharedPreferences.edit().putString("quotes_list", quotesJsonArray.toString()).apply()
                return true
            }
        }
        return false
    }
}