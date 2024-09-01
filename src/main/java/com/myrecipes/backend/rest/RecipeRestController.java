package com.myrecipes.backend.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RecipeRestController {

    @GetMapping("/recipes")
    public String getRecipes() {
        return "Recipes";
    }
}
