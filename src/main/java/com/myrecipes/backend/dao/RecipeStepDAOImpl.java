package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.RecipeStep;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class RecipeStepDAOImpl implements RecipeStepDAO {

    private EntityManager entityManager;

    @Autowired
    public RecipeStepDAOImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }

    @Override
    public List<RecipeStep> findByRecipeId(int recipeId) {
        String jpql = "SELECT rs FROM RecipeStep ri WHERE rs.recipe.id = :recipeId";
        TypedQuery<RecipeStep> query = entityManager.createQuery(jpql, RecipeStep.class);
        query.setParameter("recipeId", recipeId);
        return query.getResultList();
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
