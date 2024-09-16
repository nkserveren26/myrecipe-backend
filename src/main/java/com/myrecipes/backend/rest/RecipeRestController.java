package com.myrecipes.backend.rest;

import com.myrecipes.backend.dto.RecipeDetailsResponse;
import com.myrecipes.backend.entity.Recipe;
import com.myrecipes.backend.service.RecipeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeRestController {

    private RecipeService recipeService;

    public RecipeRestController(RecipeService theRecipeService) {
        recipeService = theRecipeService;
    }

    @GetMapping("/by-category")
    public List<Recipe> getRecipesByCategoryName(@RequestParam String categoryName) {
        return recipeService.findByCategoryName(categoryName);
    }

    @GetMapping("/{id}")
    public RecipeDetailsResponse getRecipeDetails(@PathVariable int id) {
        return recipeService.getRecipeDetails(id);
    }

}
