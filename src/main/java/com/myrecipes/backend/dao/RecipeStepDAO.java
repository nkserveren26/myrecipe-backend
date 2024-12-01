package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.RecipeStep;

import java.util.List;

public interface RecipeStepDAO {

    public List<RecipeStep> findByRecipeId(int recipeId);

    public void save(RecipeStep theRecipeStep);

    public void update(RecipeStep theRecipeStep);
}
