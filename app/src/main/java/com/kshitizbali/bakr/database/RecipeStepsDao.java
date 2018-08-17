package com.kshitizbali.bakr.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RecipeStepsDao {


    @Query("SELECT * FROM recipe_steps WHERE recipe_id = :recipeId")
    LiveData<List<RecipeStepsEntry>> loadAllRecipeSteps(int recipeId);

    @Query("SELECT * FROM recipe_steps WHERE id = :id AND recipe_id = :recipeId")
    LiveData<RecipeStepsEntry> getSingleRecipeStep(int id, int recipeId);

    @Insert
    void insertRecipeSteps(RecipeStepsEntry... recipeStepsEntries);

    @Query("SELECT * FROM recipe_steps WHERE recipe_id = :recipeId")
    List<RecipeStepsEntry> loadAllRecipeStepsById(int recipeId);

    /*@Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRecipe(RecipeEntry recipeEntry);

    @Query("UPDATE recipe SET favourite = (:fav) WHERE id = (:recipe_id)")
    void updateFavRecipe(String fav, int recipe_id);

    @Delete
    void deleteRecipe(RecipeEntry recipeEntry);*/

}
