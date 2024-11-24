package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.RecipeIngredient;

import java.util.List;

public interface RecipeIngredientDAO {
    public List<RecipeIngredient> findByRecipeId(int recipeId);
}
