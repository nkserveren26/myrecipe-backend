package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.RecipeStep;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RecipeStepDAOImpl implements RecipeStepDAO {

    private EntityManager entityManager;

    @Autowired
    public RecipeStepDAOImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }

    @Override
    public RecipeStep findByRecipeId(int recipeId) {
        return null;
    }

    @Override
    @Transactional
    public void save(RecipeStep theRecipeStep) {
        entityManager.persist(theRecipeStep);
    }

    @Override
    @Transactional
    public void update(RecipeStep theRecipeStep) {
        entityManager.merge(theRecipeStep);
    }
}
