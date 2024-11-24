package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.RecipeIngredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RecipeIngredientDAOImpl implements RecipeIngredientDAO {

    private EntityManager entityManager;

    @Autowired
    public RecipeIngredientDAOImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }
    @Override
    public List<RecipeIngredient> findByRecipeId(int recipeId) {
        String jpql = "SELECT ri FROM RecipeIngredient ri WHERE ri.recipeId = :recipeId";
        TypedQuery<RecipeIngredient> query = entityManager.createQuery(jpql, RecipeIngredient.class);
        query.setParameter("recipeId", recipeId);
        return query.getResultList();
    }
}
