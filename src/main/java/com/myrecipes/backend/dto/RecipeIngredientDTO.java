package com.myrecipes.backend.dto;

public class RecipeIngredientDTO {
    private String name;
    private String amount;

    public RecipeIngredientDTO(String name, String amount) {
        this.name = name;
        this.amount = amount;
    }
}
