package com.myrecipes.backend.dto;

import com.myrecipes.backend.entity.RecipeIngredient;
import com.myrecipes.backend.entity.RecipePoint;
import com.myrecipes.backend.entity.RecipeStep;

import java.util.List;

public class RecipeDetailsResponse {
    private int id; // レシピのID
    private String title; // レシピのタイトル
    private String videoUrl; // レシピ動画のURL
    private List<RecipeIngredient> ingredients; // 材料のリスト
    private List<RecipeStep> steps; // ステップのリスト
    private String point; // レシピのコツ・ポイント

    public RecipeDetailsResponse(
            int id, String title, String videoUrl,
            List<RecipeIngredient> ingredients, List<RecipeStep> steps, String point) {
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

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RecipeStep> steps) {
        this.steps = steps;
    }

    public String getPoint() {
        return this.point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
