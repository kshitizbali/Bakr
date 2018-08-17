package com.kshitizbali.bakr.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kshitizbali.bakr.database.AppDatabase;
import com.kshitizbali.bakr.database.RecipeEntry;
import com.kshitizbali.bakr.utilities.ConstantUtilities;

import java.util.List;

public class MainRecipeListViewModel extends AndroidViewModel {
    private static final String TAG = MainRecipeListViewModel.class.getSimpleName();

    private LiveData<List<RecipeEntry>> recipes;
    private LiveData<List<RecipeEntry>> favRecipes;

    public MainRecipeListViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        recipes = database.recipeDao().loadAllRecipes();
        favRecipes = database.recipeDao().loadTaskByFav(ConstantUtilities.FAV_YES);
    }

    public LiveData<List<RecipeEntry>> getRecipes() {
        return recipes;
    }

    public LiveData<List<RecipeEntry>> getFavRecipes() {
        return favRecipes;
    }

    /*
    private static void loadData() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {

                if (NetworkUtilities.isOnlineB()) {
                    URL recipeRequest = NetworkUtilities.buildLaunchUrl();


                    try {
                        return NetworkUtilities.getResponseFromHttpUrl(recipeRequest);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                } else {
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

            }
        }.execute();
    }
*/
}
