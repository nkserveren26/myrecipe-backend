package com.myrecipes.backend.dto;

import com.myrecipes.backend.entity.RecipeIngredient;
import com.myrecipes.backend.entity.RecipeStep;

import java.util.List;

public class RecipeDetailsResponse {
    private int id; // レシピのID
    private String name; // レシピの名前
    private String videoUrl; // レシピに関連するビデオのURL
    private List<RecipeIngredient> ingredients; // 材料のリスト
    private List<RecipeStep> steps; // ステップのリスト
}
