package com.example.foodrecipe.ui.saved;

import com.google.firebase.firestore.DocumentId;

public class RecipeData {

    String name, description, recipe_id;

    public RecipeData(){}

    @DocumentId
    public String getRecipe_id() {
        return recipe_id;
    }

    @DocumentId
    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }

    public RecipeData(String name, String description, String recipe_id) {
        this.name = name;
        this.description = description;
        this.recipe_id = recipe_id;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
