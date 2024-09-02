package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.Recipe;

import java.util.List;

public interface RecipeDAO {

    List<Recipe> findAll();

    Recipe findById(int theId);

    Recipe save(Recipe theEmployee);

    void deleteById(int theId);
}
