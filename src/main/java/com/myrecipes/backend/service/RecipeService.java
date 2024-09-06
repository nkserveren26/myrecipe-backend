package com.myrecipes.backend.service;

import com.myrecipes.backend.entity.Recipe;

import java.util.List;

public interface RecipeService {
    List<Recipe> findAll();

    List<Recipe> findByCategoryName(String categoryName);
}
