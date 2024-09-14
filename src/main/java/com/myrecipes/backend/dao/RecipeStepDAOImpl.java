package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.RecipeStep;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RecipeStepDAOImpl implements RecipeStepDAO {

    private EntityManager entityManager;

    @Autowired
    public RecipeStepDAOImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }

    @Override
    public List<RecipeStep> findStepsByRecipeId(int recipeId) {
        return List.of();
    }
}
