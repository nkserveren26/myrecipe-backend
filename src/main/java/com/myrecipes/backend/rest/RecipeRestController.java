package com.myrecipes.backend.rest;

import com.myrecipes.backend.entity.Recipe;
import com.myrecipes.backend.service.RecipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeRestController {

    private RecipeService recipeService;

    public RecipeRestController(RecipeService theRecipeService) {
        recipeService = theRecipeService;
    }

    @GetMapping("/recipes")
    public List<Recipe> getRecipes() {
        return recipeService.findAll();
    }
}
