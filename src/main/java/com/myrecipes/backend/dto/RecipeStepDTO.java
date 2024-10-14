package com.myrecipes.backend.dto;

public class RecipeStepDTO {
    private int stepNumber;
    private String description;

    public RecipeStepDTO(int number, String description) {
        this.stepNumber = number;
        this.description = description;
    }
}
