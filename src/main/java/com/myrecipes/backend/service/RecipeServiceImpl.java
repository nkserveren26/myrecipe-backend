package com.myrecipes.backend.service;

import com.myrecipes.backend.dao.RecipeDAO;
import com.myrecipes.backend.dto.RecipeDetailsResponse;
import com.myrecipes.backend.entity.Recipe;
import com.myrecipes.backend.entity.RecipeIngredient;
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
    public RecipeDetailsResponse getRecipeDetails(int id) {
        // 指定されたidのレシピを取得
        Recipe recipe = recipeDAO.findById(id);

        // 取得したレシピの材料を取得
        List<RecipeIngredient> ingredients = recipe.getIngredients();

        // 取得したレシピの手順を取得
        List<RecipeStep> steps = recipe.getSteps();

        // 取得したレシピのコツ・ポイントを取得
        String point = recipe.getRecipePoint().getPoint();

        return new RecipeDetailsResponse(
                id, recipe.getTitle(), recipe.getVideoUrl(), ingredients, steps, point
        );
    }
}
