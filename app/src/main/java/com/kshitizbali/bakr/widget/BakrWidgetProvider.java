package com.kshitizbali.bakr.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.kshitizbali.bakr.MainActivity;
import com.kshitizbali.bakr.MyFavouritesActivity;
import com.kshitizbali.bakr.R;
import com.kshitizbali.bakr.utilities.ConstantUtilities;

/**
 * Implementation of App Widget functionality.
 */
public class BakrWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager, String favCount,
                                int appWidgetId) {

        // Construct the RemoteViews object


        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bakr_widget_provider);


        Intent intent = new Intent(context, MyFavouritesActivity.class);
        intent.putExtra(ConstantUtilities.SHOW_FAV, "true");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_desert_image, pendingIntent);

        views.setTextViewText(R.id.tvFavCount, favCount);

        /*appWidgetManager.updateAppWidget(appWidgetId, views);*/
/*
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                views.setTextViewText(R.id.tvFavCount, String.valueOf(AppDatabase.getInstance(context).recipeDao().getFavRecipeCount()));
            }
        });*/


       /* Intent intent = new Intent(context, BakrFavouriteService.class);
        intent.putExtra(ConstantUtilities.SHOW_FAV, "true");
        intent.setAction(BakrFavouriteService.ACTION_FAV_RECIPES);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.widget_desert_image, pendingIntent);*/

        /*Intent bakrIntent = new Intent(context, BakrFavouriteService.class);
        bakrIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        bakrIntent.setAction(BakrFavouriteService.ACTION_FAV_RECIPES);
        PendingIntent bakrPendingIntent = PendingIntent.getService(
                context,
                0,
                bakrIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_desert_image, bakrPendingIntent);*/


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        BakrFavouriteService.startActionShowFav(context);
      /*  for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }*/
    }


    public static void updateBakrFavWidget(Context context, AppWidgetManager appWidgetManager,
                                           String favCount, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, favCount, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        BakrFavouriteService.startActionShowFav(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

