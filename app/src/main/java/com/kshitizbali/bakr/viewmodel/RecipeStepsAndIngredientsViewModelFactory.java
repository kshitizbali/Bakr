package com.kshitizbali.bakr.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.kshitizbali.bakr.database.AppDatabase;

public class RecipeStepsAndIngredientsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private AppDatabase mDb;
    private final int recipeId, recipeStepId;

    public RecipeStepsAndIngredientsViewModelFactory(AppDatabase appDatabase, int mRecipeId, int mRecipeStepId) {
        mDb = appDatabase;
        recipeId = mRecipeId;
        recipeStepId = mRecipeStepId;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new RecipeStepsAndIngredientsViewModel(mDb, recipeId, recipeStepId);
    }
}
