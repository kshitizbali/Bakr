package com.kshitizbali.bakr.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.kshitizbali.bakr.AppExecutors;
import com.kshitizbali.bakr.MainActivity;
import com.kshitizbali.bakr.R;
import com.kshitizbali.bakr.database.AppDatabase;
import com.kshitizbali.bakr.utilities.ConstantUtilities;

/**
 * Implementation of App Widget functionality.
 */
public class BakrStarRecipeWidgetProvider extends AppWidgetProvider {
    public static final String EXTRA_LABEL = "TASK_TEXT";

    static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object

        RemoteViews views = new RemoteViews(

                context.getPackageName(),
                R.layout.collection_widget

        );

        // click event handler for the title, launches the app when the user clicks on title
        Intent titleIntent = new Intent(context, MainActivity.class);
        PendingIntent titlePendingIntent = PendingIntent.getActivity(context, 0, titleIntent, 0);
        views.setOnClickPendingIntent(R.id.widgetTitleLabel, titlePendingIntent);

        Intent intent = new Intent(context, MyWidgetRemoteViewsService.class);
        views.setRemoteAdapter(R.id.widgetListView, intent);

        // template to handle the click listener for each item
        Intent clickIntentTemplate = new Intent(context, MainActivity.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widgetListView, clickPendingIntentTemplate);
/*
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bakr_widget_provider);


        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(ConstantUtilities.SHOW_FAV, "true");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_desert_image, pendingIntent);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                views.setTextViewText(R.id.tvFavCount, String.valueOf(AppDatabase.getInstance(context).recipeDao().getFavRecipeCount()));

            }
        });*/


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action != null) {
            if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
                // refresh all your widgets
                AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                ComponentName cn = new ComponentName(context, BakrStarRecipeWidgetProvider.class);
                mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widgetListView);
            }
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {


            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, BakrStarRecipeWidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        sendRefreshBroadcast(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        /*sendRefreshBroadcast(context);*/
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

