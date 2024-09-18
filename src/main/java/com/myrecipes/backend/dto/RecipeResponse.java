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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
