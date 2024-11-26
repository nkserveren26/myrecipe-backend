package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.Recipe;
import com.myrecipes.backend.entity.RecipeIngredient;

import java.util.List;

public interface RecipeIngredientDAO {
    public List<RecipeIngredient> findByRecipeId(int recipeId);

    public void save(RecipeIngredient theRecipeIngredient);

    public void update(RecipeIngredient theRecipeIngredient);

}
