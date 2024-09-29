package com.myrecipes.backend.service;

import com.myrecipes.backend.dao.CategoryDAO;
import com.myrecipes.backend.dao.RecipeDAO;
import com.myrecipes.backend.dto.AddRecipeRequestDTO;
import com.myrecipes.backend.dto.RecipeDetailsResponse;
import com.myrecipes.backend.dto.RecipeResponse;
import com.myrecipes.backend.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService{

    private RecipeDAO recipeDAO;
    private CategoryDAO categoryDAO;

    @Autowired
    public RecipeServiceImpl(RecipeDAO theRecipeDAO, CategoryDAO theCategoryDAO) {
        recipeDAO = theRecipeDAO;
        categoryDAO = theCategoryDAO;
    }

    @Override
    public List<Recipe> findAll() {
        return recipeDAO.findAll();
    }

    @Override
    public List<RecipeResponse> findRecipeByCategoryName(String categoryName) {
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

    @Override
    public Category findCategoryByName(String categoryName) {
        return categoryDAO.findCategoryByName(categoryName);
    }

    @Override
    public void AddRecipe(AddRecipeRequestDTO addRecipeRequestDTO, MultipartFile thumbnail) {

        System.out.println("Adding Recipe.");

        // Recipeインスタンス生成
        Recipe recipe = new Recipe();

        // Recipeインスタンスの各フィールドに値をセット
        recipe.setTitle(addRecipeRequestDTO.getTitle());
        recipe.setServings(addRecipeRequestDTO.getServings());
        recipe.setVideoUrl(addRecipeRequestDTO.getVideoUrl());
        recipe.setIngredients(addRecipeRequestDTO.getIngredients());
        recipe.setSteps(addRecipeRequestDTO.getSteps());

        // RecipePointインスタンスを生成し、RecipeのrecipePointフィールドにセット
        RecipePoint recipePoint = new RecipePoint(addRecipeRequestDTO.getPoint());
        recipe.setRecipePoint(recipePoint);

        // 日本時間の現在時刻を取得
        ZoneId zoneId = ZoneId.of("Asia/Tokyo");
        LocalDateTime jstNow = LocalDateTime.now(zoneId);

        // createdAtフィールドに日本時間の現在時刻を設定
        recipe.setCreatedAt(jstNow);

        // categoryフィールドにCategoryインスタンスをセット
        Category category = findCategoryByName(addRecipeRequestDTO.getCategory());
        recipe.setCategory(category);

        System.out.println(recipe);


        // 画像をS3にアップロード
        recipe.setImage("aqqua_pazza.jpg");

        // 署名付きURLをRecipeのimageフィールドにセット
        // レシピにセットされた各材料のrecipeフィールドに対象レシピを設定
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                ingredient.setRecipe(recipe);
            }
        }

        // レシピにセットされた各ステップのrecipeフィールドに対象レシピを設定
        if (recipe.getSteps() != null) {
            for (RecipeStep step : recipe.getSteps()) {
                step.setRecipe(recipe);
            }
        }

        // レシピにセットされた、レシピのコツのrecipeフィールドに対象レシピを設定
        if (recipe.getRecipePoint() != null) {
            recipe.getRecipePoint().setRecipe(recipe);
        }

        recipeDAO.save(recipe);
    }
}
