package com.myrecipes.backend.dto;

import com.myrecipes.backend.entity.RecipeIngredient;
import com.myrecipes.backend.entity.RecipeStep;

import java.util.List;

public class RecipeDetailsResponse {
    private int id; // レシピのID
    private String title; // レシピのタイトル
    private String videoUrl; // レシピ動画のURL
    private List<RecipeIngredientDTO> ingredients; // 材料のリスト
    private List<RecipeStepDTO> steps; // ステップのリスト
    private String point; // レシピのコツ・ポイント

    public RecipeDetailsResponse(
            int id, String title, String videoUrl,
            List<RecipeIngredientDTO> ingredients, List<RecipeStepDTO> steps, String point) {
        this.id = id;
        this.title = title;
        this.videoUrl = videoUrl;
        this.ingredients = ingredients;
        this.steps = steps;
        this.point = point;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<RecipeIngredientDTO> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }

    public List<RecipeStepDTO> getSteps() {
        return steps;
    }

    public void setSteps(List<RecipeStepDTO> steps) {
        this.steps = steps;
    }

    public String getPoint() {
        return this.point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
