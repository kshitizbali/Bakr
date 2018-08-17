package com.kshitizbali.bakr.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RecipeIngredientsDao {

    @Query("SELECT * FROM recipe_ingredients ORDER BY id")
    LiveData<List<RecipeIngredientsEntry>> loadAllRecipeIngredients();

    @Query("SELECT * FROM recipe_ingredients WHERE recipe_id =  :recipeId")
    LiveData<List<RecipeIngredientsEntry>> loadRecipeIngredientsById(int recipeId);

    @Query("SELECT * FROM recipe_ingredients WHERE recipe_id =  :recipeId")
    List<RecipeIngredientsEntry> loadStarRecipeIngredientsById(int recipeId);

    @Insert
    void insertRecipeIngredients(RecipeIngredientsEntry... recipeIngredientsEntries);

   /* @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRecipeIngredients(RecipeIngredientsEntry recipeIngredientsEntry);

    @Query("UPDATE recipe SET favourite = (:fav) WHERE id = (:recipe_id)")
    void updateFavRecipe(String fav, int recipe_id);

    @Delete
    void deleteRecipe(RecipeEntry recipeEntry);*/
}
