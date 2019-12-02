package com.example.musicwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class MusicWidgetClass extends AppWidgetProvider {

    private static final String TAG = "Widget";

    public final static String ACTION_PLAY = "PlaySong";
    public final static String ACTION_PAUSE = "PauseSong";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_widget_class);
//        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static PendingIntent getPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, MusicWidgetClass.class);
        intent.setAction(action);
//        return PendingIntent.getBroadcast(context, 0, intent, 0);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

//        Intent playIntent = new Intent(context, MusicService.class);
//
//        Intent pauseIntent = new Intent(context, MusicService.class);
//
//        Intent stopIntent = new Intent(context, MusicService.class);
//
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_widget_class);
//
//        PendingIntent playPendingIntent = PendingIntent.getService(
//                context, REQUEST_CODE, playIntent, INTENT_FLAGS);
//        PendingIntent pausePendingIntent = PendingIntent.getService(
//                context, REQUEST_CODE, pauseIntent, INTENT_FLAGS);
//        PendingIntent stopPendingIntent = PendingIntent.getService(
//                context, REQUEST_CODE, stopIntent, INTENT_FLAGS);
//
//        views.setOnClickPendingIntent(R.id.play_btn, playPendingIntent);
//        views.setOnClickPendingIntent(R.id.pause_bnt, pausePendingIntent);

        RemoteViews remoteViews = getRemoteViews(context);

        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }

        ComponentName thisWidget = new ComponentName(context, MusicWidgetClass.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(thisWidget, remoteViews);
    }

    public static RemoteViews getRemoteViews(Context context){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.music_widget_class);

        // For Play/Pause button
        PendingIntent pendingIntentStart = getPendingIntent(context, MusicWidgetClass.ACTION_PLAY);
        remoteViews.setOnClickPendingIntent(R.id.play_btn, pendingIntentStart);

//        // For Stop button
//        PendingIntent pendingIntentStop = getPendingIntent(context, MusicWidget.ACTION_STOP);
//        remoteViews.setOnClickPendingIntent(R.id.button_stop,pendingIntentStop);

        PendingIntent pendingIntentShuffle = getPendingIntent(context, MusicWidgetClass.ACTION_PAUSE);
        remoteViews.setOnClickPendingIntent(R.id.pause_bnt,pendingIntentShuffle);

        // For Song List activity
        Intent intent= new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.chose_btn, pendingIntent);

        return remoteViews;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final String action = intent.getAction();
        Log.d(TAG, "Widget received action: " + action);

        if ((action.equals(ACTION_PLAY) || action.equals(ACTION_PAUSE)))
        {
            Intent serviceIntent = new Intent(context, MusicService.class);
            serviceIntent.setAction(action);
            context.startService(serviceIntent);
        }
        else
        {
            super.onReceive(context, intent);
        }
    }
}

