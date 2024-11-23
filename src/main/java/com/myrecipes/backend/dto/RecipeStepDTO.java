package com.myrecipes.backend.dto;

public class RecipeStepDTO {
    private int id;
    private int stepNumber;
    private String description;

    public RecipeStepDTO(int id, int number, String description) {
        this.id = id;
        this.stepNumber = number;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
