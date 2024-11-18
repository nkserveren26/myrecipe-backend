package com.myrecipes.backend.rest;

import com.myrecipes.backend.dto.AddRecipeRequest;
import com.myrecipes.backend.dto.RecipeDetailsResponse;
import com.myrecipes.backend.dto.RecipeResponse;
import com.myrecipes.backend.dto.UpdateRecipeRequest;
import com.myrecipes.backend.service.RecipeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public void addRecipe(@RequestPart("recipe") AddRecipeRequest addRecipeRequest, @RequestPart("thumbnail") MultipartFile thumbnail) {

        // レシピを新規登録
        recipeService.addRecipe(addRecipeRequest, thumbnail);
    }

    @PutMapping("/{id}")
    public void updateRecipe(@PathVariable int id, @RequestPart("recipe") UpdateRecipeRequest updateRecipeRequest, @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {

        // レシピを更新
        recipeService.updateRecipe(id, updateRecipeRequest, thumbnail);
        System.out.println("Completed updating recipe.");
    }

}
