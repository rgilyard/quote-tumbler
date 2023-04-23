package com.rgilyard.quotetumbler

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class PointlessWidget : AppWidgetProvider() {
    companion object {
        private const val TOGGLE_ACTION = "com.rgilyard.quotetumbler.TOGGLE_ACTION"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updatePointlessWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updatePointlessWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.pointless_widget)

        val toggleIntent = Intent(context, PointlessWidget::class.java)
        toggleIntent.action = TOGGLE_ACTION
        toggleIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        val togglePendingIntent = PendingIntent.getBroadcast(context, appWidgetId * 100,
            toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.toggle_image, togglePendingIntent)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (TOGGLE_ACTION == intent.action) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val views = RemoteViews(context.packageName, R.layout.pointless_widget)

                val sharedPreferences = context.getSharedPreferences("ToggleImageWidgetPrefs", Context.MODE_PRIVATE)
                val currentImageTag = sharedPreferences.getString("widget_$appWidgetId", "pointless_widget_frame_1")

                if ("pointless_widget_frame_1" == currentImageTag) {
                    views.setImageViewResource(R.id.toggle_image, R.drawable.pointless_widget_frame_2)
                    sharedPreferences.edit().putString("widget_$appWidgetId", "pointless_widget_frame_2").apply()
                } else {
                    views.setImageViewResource(R.id.toggle_image, R.drawable.pointless_widget_frame_1)
                    sharedPreferences.edit().putString("widget_$appWidgetId", "pointless_widget_frame_1").apply()
                }
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

