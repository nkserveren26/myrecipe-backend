package com.myrecipes.backend.service;

import com.myrecipes.backend.dto.RecipeDetailsResponse;
import com.myrecipes.backend.dto.RecipeResponse;
import com.myrecipes.backend.entity.Recipe;
import com.myrecipes.backend.entity.RecipeStep;

import java.util.List;

public interface RecipeService {
    List<Recipe> findAll();

    List<RecipeResponse> findByCategoryName(String categoryName);

    public RecipeDetailsResponse getRecipeDetails(int id);
}
