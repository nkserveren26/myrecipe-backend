package com.myrecipes.backend.rest;

import com.myrecipes.backend.entity.Recipe;
import com.myrecipes.backend.entity.RecipeStep;
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

    @GetMapping("/recipes")
    public List<Recipe> getRecipes() {
        return recipeService.findAll();
    }

    @GetMapping("/by-category")
    public List<Recipe> getRecipesByCategoryName(@RequestParam String categoryName) {
        return recipeService.findByCategoryName(categoryName);
    }

    @GetMapping("/{id}/steps")
    public List<RecipeStep> getRecipeSteps(@PathVariable int id) {
        return recipeService.getRecipeSteps(id);
    }

}
