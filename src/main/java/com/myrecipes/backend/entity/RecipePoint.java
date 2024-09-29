package com.myrecipes.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "recipe_point")
public class RecipePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "point")
    private String point;

    public RecipePoint(String point) {
        this.point = point;
    }

    // Recipeとの1対1のリレーションを定義
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "RecipePoint{" +
                "id=" + id +
                ", point='" + point + '\'' +
                '}';
    }
}
