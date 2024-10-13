package com.myrecipes.backend.dto;

public class RecipeStepDTO {
    private String stepNumber;
    private String description;

    public RecipeStepDTO(String number, String description) {
        this.stepNumber = number;
        this.description = description;
    }
}
