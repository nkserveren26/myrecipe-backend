package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.RecipeIngredient;
import com.myrecipes.backend.entity.RecipePoint;

import java.util.List;

public interface RecipePointDAO {

    public RecipePoint findByRecipeId(int recipeId);
}
