package com.myrecipes.backend.service;

import com.myrecipes.backend.dto.AddRecipeRequestDTO;
import com.myrecipes.backend.dto.RecipeDetailsResponse;
import com.myrecipes.backend.dto.RecipeResponse;
import com.myrecipes.backend.entity.Category;
import com.myrecipes.backend.entity.Recipe;
import com.myrecipes.backend.entity.RecipeStep;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecipeService {
    List<Recipe> findAll();

    List<RecipeResponse> findRecipeByCategoryName(String categoryName);

    public RecipeDetailsResponse getRecipeDetails(int id);

    public Category findCategoryByName(String categoryName);

    public void AddRecipe(AddRecipeRequestDTO addRecipeRequestDTO, MultipartFile thumbnail);
}
