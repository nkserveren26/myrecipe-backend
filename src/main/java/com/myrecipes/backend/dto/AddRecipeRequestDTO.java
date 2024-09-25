package com.myrecipes.backend.dto;

import com.myrecipes.backend.entity.RecipeIngredient;
import com.myrecipes.backend.entity.RecipeStep;

import java.util.List;

public class AddRecipeRequestDTO {

    private String title;
    private int servings;
    private String image;
    private String videoUrl;
    private String category; // カテゴリー名を文字列で受け取る
    private List<RecipeIngredient> ingredients;
    private List<RecipeStep> steps;
    private String point;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "AddRecipeRequestDTO{" +
                "title='" + title + '\'' +
                ", servings=" + servings +
                ", image='" + image + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", category='" + category + '\'' +
                ", ingredients=" + ingredients +
                ", steps=" + steps +
                ", point='" + point + '\'' +
                '}';
    }
}
