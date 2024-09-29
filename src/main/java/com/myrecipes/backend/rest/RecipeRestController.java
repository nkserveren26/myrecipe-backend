package com.myrecipes.backend.rest;

import com.myrecipes.backend.dto.AddRecipeRequestDTO;
import com.myrecipes.backend.dto.RecipeDetailsResponse;
import com.myrecipes.backend.dto.RecipeResponse;
import com.myrecipes.backend.entity.Category;
import com.myrecipes.backend.entity.Recipe;
import com.myrecipes.backend.entity.RecipePoint;
import com.myrecipes.backend.service.RecipeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeRestController {

    private RecipeService recipeService;

    public RecipeRestController(RecipeService theRecipeService) {
        recipeService = theRecipeService;
    }

    @GetMapping("/by-category")
    public List<RecipeResponse> getRecipesByCategoryName(@RequestParam String categoryName) {
        return recipeService.findRecipeByCategoryName(categoryName);
    }

    @GetMapping("/{id}")
    public RecipeDetailsResponse getRecipeDetails(@PathVariable int id) {
        return recipeService.getRecipeDetails(id);
    }

    @PostMapping
    public void addRecipe(@RequestPart("recipe") AddRecipeRequestDTO addRecipeRequestDTO, @RequestPart("thumbnail") MultipartFile thumbnail) {

        // レシピを新規登録
        recipeService.AddRecipe(addRecipeRequestDTO, thumbnail);
    }

}
