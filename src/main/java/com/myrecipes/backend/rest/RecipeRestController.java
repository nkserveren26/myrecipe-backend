package com.myrecipes.backend.rest;

import com.myrecipes.backend.dto.AddRecipeRequest;
import com.myrecipes.backend.dto.RecipeDetailsResponse;
import com.myrecipes.backend.dto.RecipeResponse;
import com.myrecipes.backend.dto.UpdateRecipeRequest;
import com.myrecipes.backend.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeRestController {

    private RecipeService recipeService;

    public RecipeRestController(RecipeService theRecipeService) {
        recipeService = theRecipeService;
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<RecipeResponse>> getRecipesByCategoryName(@RequestParam String categoryName) {
        try {
            // サービス層からカテゴリに該当するレシピ一覧を取得
            List<RecipeResponse> recipes = recipeService.findRecipeByCategoryName(categoryName);

            if (recipes.isEmpty()) {
                // レシピが空の場合、404を返却
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(recipes); // レシピ一覧と200を返却
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500エラーを返却
        }
    }

    @GetMapping("/{id}")
    public RecipeDetailsResponse getRecipeDetails(@PathVariable int id) {
        return recipeService.getRecipeDetails(id);
    }

    @PostMapping
    public ResponseEntity<Void> addRecipe(@RequestPart("recipe") AddRecipeRequest addRecipeRequest, @RequestPart("thumbnail") MultipartFile thumbnail) {

        try {
            recipeService.addRecipe(addRecipeRequest, thumbnail);
            return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRecipe(@PathVariable int id, @RequestPart("recipe") UpdateRecipeRequest updateRecipeRequest, @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {

        try {
            // レシピを更新
            recipeService.updateRecipeById(id, updateRecipeRequest, thumbnail);

            // 成功時に200 OKを返す
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable int id) {
        try {
            recipeService.deleteRecipeById(id);

            // 成功時に200 OKを返す
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }
}
