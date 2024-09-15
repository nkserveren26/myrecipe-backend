package com.myrecipes.backend.service;

import com.myrecipes.backend.dao.RecipeDAO;
import com.myrecipes.backend.entity.Recipe;
import com.myrecipes.backend.entity.RecipeStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService{

    private RecipeDAO recipeDAO;

    @Autowired
    public RecipeServiceImpl(RecipeDAO theRecipeDAO) {
        recipeDAO = theRecipeDAO;
    }

    @Override
    public List<Recipe> findAll() {
        return recipeDAO.findAll();
    }

    @Override
    public List<Recipe> findByCategoryName(String categoryName) {
        return recipeDAO.findByCategoryName(categoryName);
    }

    @Override
    public List<RecipeStep> getRecipeDetails(int id) {
        Recipe recipe = recipeDAO.findById(id);
        return recipe.getSteps();
    }
}
