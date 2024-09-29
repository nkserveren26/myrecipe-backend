package com.myrecipes.backend.dao;

import com.myrecipes.backend.dto.RecipeResponse;
import com.myrecipes.backend.entity.Recipe;

import java.util.List;

public interface RecipeDAO {

    public List<Recipe> findAll();

    public Recipe findById(int theId);

    public void save(Recipe theEmployee);

    public void deleteById(int theId);

    public List<RecipeResponse> findByCategoryName(String categoryName);
}
