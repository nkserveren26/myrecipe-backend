package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.RecipeStep;

public interface RecipeStepDAO {

    public RecipeStep findByRecipeId(int recipeId);

    public void save(RecipeStep theRecipeStep);

    public void update(RecipeStep theRecipeStep);
}
