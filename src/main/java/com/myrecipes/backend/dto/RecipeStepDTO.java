package com.myrecipes.backend.dto;

public class RecipeStepDTO {
    private int stepNumber;
    private String description;

    public RecipeStepDTO(int number, String description) {
        this.stepNumber = number;
        this.description = description;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
