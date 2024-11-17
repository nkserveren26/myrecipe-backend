package com.myrecipes.backend.dto;

import java.time.LocalDateTime;

public class RecipeResponse {
    private int id;
    private String title;
    private String image;
    private int servings;
    private String videoUrl;
    private LocalDateTime createdAt;

    public RecipeResponse(int id, String title, String image, int servings, String videoUrl, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.servings = servings;
        this.videoUrl = videoUrl;
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

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
