package com.kshitizbali.bakr.widget;

import android.app.IntentService;
import android.app.Notification;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.kshitizbali.bakr.AppExecutors;
import com.kshitizbali.bakr.R;
import com.kshitizbali.bakr.database.AppDatabase;

public class BakrFavouriteService extends IntentService {


    public static final String SHOW_FAV = "show_fav";

    public static final String ACTION_FAV_RECIPES =
            "com.kshitizbali.bakr.action.fav_recipes";

    public BakrFavouriteService() {
        super("BakrFavouriteService");
    }


    /* @Override
     public void onCreate() {
         super.onCreate();
         startForeground(1,new Notification());
     }
 */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FAV_RECIPES.equals(action)) {
                handleActionShowFav(this);
            }
        }
    }


    public static void startActionShowFav(Context context) {
        /*Log.i("", getIntent().getExtras().getBoolean(ConstantUtilities.SHOW_FAV));*/
        Intent intent = new Intent(context, BakrFavouriteService.class);
        intent.setAction(ACTION_FAV_RECIPES);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, intent);
        } else {
            context.startService(intent);
        }*/
        context.startService(intent);
    }

    public void handleActionShowFav(final Context context) {
        final String[] favCount = new String[1];

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                favCount[0] = String.valueOf(AppDatabase.getInstance(context).recipeDao().getFavRecipeCount());
                if (favCount[0].equals("")) {
                    favCount[0] = "0";
                }
            }
        });
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakrWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.tvFavCount);
        //Now update all widgets
        BakrWidgetProvider.updateBakrFavWidget(this, appWidgetManager, favCount[0], appWidgetIds);

       /* Intent intent = new Intent(context, BakrFavouriteService.class);
        intent.setAction(ACTION_FAV_RECIPES);
        context.startService(intent);*/
    }
}
