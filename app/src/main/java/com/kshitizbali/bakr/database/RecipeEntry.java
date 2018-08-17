package com.kshitizbali.bakr.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "recipe")
public class RecipeEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String image;
    private String servings;
    private String favourite;

    @Ignore
    public RecipeEntry(String name, String image, String servings, String favourite) {
        this.name = name;
        this.image = image;
        this.servings = servings;
        this.favourite = favourite;
    }

    public RecipeEntry(int id, String name, String image, String servings, String favourite) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.servings = servings;
        this.favourite = favourite;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }
}
