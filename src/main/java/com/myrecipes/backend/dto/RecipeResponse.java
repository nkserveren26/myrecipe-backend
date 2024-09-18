package com.myrecipes.backend.dto;

import java.time.LocalDateTime;

public class RecipeResponse {
    private int id;
    private String title;
    private String image;
    private LocalDateTime createdAt;

    public RecipeResponse(int id, String title, String image, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.createdAt = createdAt;
    }
}
