package com.rgilyard.quotetumbler

import android.content.Context
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import com.rgilyard.quotetumbler.R
import org.json.JSONArray

/**
 * Implementation of App Widget functionality.
 */
class QuoteWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateQuoteWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        startAlarm(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        cancelAlarm(context)
    }
}

internal fun updateQuoteWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val quote: String = getQuote(context)

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.quote_widget)
    views.setTextViewText(R.id.appwidget_text, quote)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

fun getQuote(context: Context): String {
    // Get random quote from quote assets
    // Load quotes
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("quotes", Context.MODE_PRIVATE)
    val quotesJsonArray = JSONArray(sharedPreferences.getString("quotes_list", "[]"))
    val quotesList = mutableListOf<String>()
    for (i in 0 until quotesJsonArray.length()) {
        quotesList.add(quotesJsonArray.getString(i))
    }
    return quotesList.shuffled().take(1)[0]
}

fun startAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, QuoteWidgetAlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    val interval: Long = 30 * 60 * 1000 // Update every 30 minutes
    alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pendingIntent)
}

fun cancelAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, QuoteWidgetAlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    alarmManager.cancel(pendingIntent)
}