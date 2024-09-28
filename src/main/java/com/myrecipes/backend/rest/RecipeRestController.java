package com.myrecipes.backend.rest;

import com.myrecipes.backend.dto.AddRecipeRequestDTO;
import com.myrecipes.backend.dto.RecipeDetailsResponse;
import com.myrecipes.backend.dto.RecipeResponse;
import com.myrecipes.backend.entity.Recipe;
import com.myrecipes.backend.service.RecipeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeRestController {

    private RecipeService recipeService;

    public RecipeRestController(RecipeService theRecipeService) {
        recipeService = theRecipeService;
    }

    @GetMapping("/by-category")
    public List<RecipeResponse> getRecipesByCategoryName(@RequestParam String categoryName) {
        return recipeService.findByCategoryName(categoryName);
    }

    @GetMapping("/{id}")
    public RecipeDetailsResponse getRecipeDetails(@PathVariable int id) {
        return recipeService.getRecipeDetails(id);
    }

    @PostMapping
    public void addRecipe(@RequestPart("recipe") AddRecipeRequestDTO addRecipeRequestDTO,@RequestPart("thumbnail") MultipartFile thumbnail) {
        System.out.println("Adding Recipe.");
        System.out.println(addRecipeRequestDTO);
        System.out.println(thumbnail);

        // Recipeインスタンス生成
        Recipe recipe = new Recipe();

        // Recipeインスタンスの各フィールドに値をセット
        recipe.setTitle(addRecipeRequestDTO.getTitle());
        recipe.setServings(addRecipeRequestDTO.getServings());
        recipe.setVideoUrl(addRecipeRequestDTO.getVideoUrl());

        // 日本時間の現在時刻を取得
        ZoneId zoneId = ZoneId.of("Asia/Tokyo");
        LocalDateTime jstNow = LocalDateTime.now(zoneId);

        // createdAtフィールドに日本時間を設定
        recipe.setCreatedAt(jstNow);

        // 画像をS3にアップロード

        // 署名付きURLをRecipeのimageフィールドにセット

        // レシピを新規登録
    }

}
