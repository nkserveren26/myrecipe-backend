package com.myrecipes.backend.service;

import com.myrecipes.backend.dto.AddRecipeRequest;
import com.myrecipes.backend.dto.RecipeDetailsResponse;
import com.myrecipes.backend.dto.RecipeResponse;
import com.myrecipes.backend.entity.Category;
import com.myrecipes.backend.entity.Recipe;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecipeService {
    List<Recipe> findAll();

    List<RecipeResponse> findRecipeByCategoryName(String categoryName);

    public RecipeDetailsResponse getRecipeDetails(int id);

    public Category findCategoryByName(String categoryName);

    public void addRecipe(AddRecipeRequest addRecipeRequest, MultipartFile thumbnail);
}
