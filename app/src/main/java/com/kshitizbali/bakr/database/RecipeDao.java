package com.kshitizbali.bakr.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipe ORDER BY id")
    LiveData<List<RecipeEntry>> loadAllRecipes();

    @Query("SELECT * FROM recipe WHERE id = (:id)")
    LiveData<RecipeEntry> loadTaskById(int id);

    @Query("SELECT * FROM recipe WHERE favourite LIKE (:isFav)")
    LiveData<List<RecipeEntry>> loadTaskByFav(String isFav);


    /*@Query("SELECT COUNT(id) FROM recipe WHERE is_checked = 1")*/
    @Query("SELECT COUNT(id) FROM recipe")
    int getNumberOfRows();


    @Query("SELECT COUNT(*) AS COUNT FROM recipe WHERE favourite = 'fav'")
    int getFavRecipeCount();

    @Insert
    void insertRecipe(RecipeEntry... recipeEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRecipe(RecipeEntry recipeEntry);

    @Query("UPDATE recipe SET favourite = (:fav) WHERE id = (:recipe_id)")
    void updateFavRecipe(String fav, int recipe_id);

    @Delete
    void deleteRecipe(RecipeEntry recipeEntry);

}
