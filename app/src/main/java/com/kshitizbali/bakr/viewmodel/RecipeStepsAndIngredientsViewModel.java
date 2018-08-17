package com.kshitizbali.bakr.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.kshitizbali.bakr.database.AppDatabase;
import com.kshitizbali.bakr.database.RecipeEntry;
import com.kshitizbali.bakr.database.RecipeIngredientsEntry;
import com.kshitizbali.bakr.database.RecipeStepsEntry;

import java.util.List;

public class RecipeStepsAndIngredientsViewModel extends ViewModel {

    /*private LiveData<RecipeEntry> mRecipe;*/
    private LiveData<List<RecipeIngredientsEntry>> mRecipeIngre;
    private LiveData<List<RecipeStepsEntry>> mRecipeStepsList;
    private LiveData<RecipeStepsEntry> mRecipeStepsSelected;

    public RecipeStepsAndIngredientsViewModel(AppDatabase database, int recipeId, int recipeStepId) {
        /*mRecipe = database.recipeDao().loadTaskById(recipeId);*/
        mRecipeIngre = database.recipeIngredientsDao().loadRecipeIngredientsById(recipeId);
        mRecipeStepsList = database.recipeStepsDao().loadAllRecipeSteps(recipeId);

        /*if (recipeStepId == 0) {
            return;
        }*/
        mRecipeStepsSelected = database.recipeStepsDao().getSingleRecipeStep(recipeStepId, recipeId);



    }

    /*public LiveData<RecipeEntry> getRecipe() {
        return mRecipe;
    }*/

    public LiveData<List<RecipeIngredientsEntry>> getmRecipeIngre() {
        return mRecipeIngre;
    }

    public LiveData<List<RecipeStepsEntry>> getmRecipeSteps() {
        return mRecipeStepsList;
    }

    public LiveData<RecipeStepsEntry> getSelectedRecipeSteps() {

        //Log.i("Model", ""+mRecipeStepsSelected.getValue().getDescription());
        return mRecipeStepsSelected;
    }
}
