package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.RecipeStep;

import java.util.List;

public interface RecipeStepDAO {
    public List<RecipeStep> findStepsByRecipeId(int recipeId);
}
