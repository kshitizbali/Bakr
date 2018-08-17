package com.kshitizbali.bakr.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.kshitizbali.bakr.AppExecutors;
import com.kshitizbali.bakr.R;
import com.kshitizbali.bakr.database.AppDatabase;
import com.kshitizbali.bakr.database.RecipeIngredientsEntry;
import com.kshitizbali.bakr.utilities.ConstantUtilities;
import com.kshitizbali.bakr.utilities.SavePreferences;

import java.util.List;

public class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private AppDatabase appDatabase;


    private List<RecipeIngredientsEntry> mRecipeIngreList;


    public List<RecipeIngredientsEntry> getmRecipeIngreList() {
        return mRecipeIngreList;
    }

    public void setmRecipeIngreList(List<RecipeIngredientsEntry> mRecipeIngreList) {
        this.mRecipeIngreList = mRecipeIngreList;
    }

    public MyWidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        appDatabase = AppDatabase.getInstance(mContext);
    }

    @Override
    public void onCreate() {
       /* final long identityToken = Binder.clearCallingIdentity();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                setmRecipeIngreList(appDatabase.recipeIngredientsDao().loadStarRecipeIngredientsById(Integer.parseInt(SavePreferences.fetchString(mContext, ConstantUtilities.STAR_RECIPE_PREF, ConstantUtilities.DEF_RECIPE_STAR))));
                //mRecipeStepsList = appDatabase.recipeStepsDao().loadAllRecipeSteps(Integer.parseInt(SavePreferences.fetchString(mContext, ConstantUtilities.STAR_RECIPE_PREF, ConstantUtilities.DEF_RECIPE_STAR)));
            }
        });
      *//*  Uri uri = Contract.PATH_TODOS_URI;
        mCursor = mContext.getContentResolver().query(uri,
                null,
                null,
                null,
                Contract._ID + " DESC");*//*

        Binder.restoreCallingIdentity(identityToken);
*/
    }

    @Override
    public void onDataSetChanged() {

        final long identityToken = Binder.clearCallingIdentity();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                setmRecipeIngreList(appDatabase.recipeIngredientsDao().loadStarRecipeIngredientsById(Integer.parseInt(SavePreferences.fetchString(mContext, ConstantUtilities.STAR_RECIPE_PREF, ConstantUtilities.DEF_RECIPE_STAR))));
                //mRecipeStepsList = appDatabase.recipeStepsDao().loadAllRecipeSteps(Integer.parseInt(SavePreferences.fetchString(mContext, ConstantUtilities.STAR_RECIPE_PREF, ConstantUtilities.DEF_RECIPE_STAR)));
            }
        });
      /*  Uri uri = Contract.PATH_TODOS_URI;
        mCursor = mContext.getContentResolver().query(uri,
                null,
                null,
                null,
                Contract._ID + " DESC");*/

        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {
        /*if (appDatabase != null) {
            appDatabase.close();
        }*/
    }

    @Override
    public int getCount() {
        return mRecipeIngreList == null ? 0 : mRecipeIngreList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                appDatabase == null || mRecipeIngreList.size() == 0) {
            return null;
        }

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.collection_widget_list_item);
        /*mCursor.getString(1)*/
        rv.setTextViewText(R.id.widgetItemTaskNameLabel, mRecipeIngreList.get(position).getIngredient());

        Intent fillInIntent = new Intent();/*mCursor.getString(1)*/
        fillInIntent.putExtra(BakrStarRecipeWidgetProvider.EXTRA_LABEL, mRecipeIngreList.get(position).getIngredient());
        rv.setOnClickFillInIntent(R.id.widgetItemContainer, fillInIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        /*return mCursor.moveToPosition(position) ? mCursor.getLong(0) : position;*/
        if (mRecipeIngreList.get(position).getId() != 0) {
            return mRecipeIngreList.get(position).getId();
        } else {
            return 0;
        }

    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
