package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.Recipe;
import com.myrecipes.backend.entity.RecipeStep;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecipeDAOImpl implements RecipeDAO {

    private EntityManager entityManager;

    @Autowired
    public RecipeDAOImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }

    @Override
    public List<Recipe> findAll() {
        TypedQuery<Recipe> theQuery = entityManager.createQuery("from Recipe", Recipe.class);

        List<Recipe> recipes = theQuery.getResultList();

        return recipes;
    }

    @Override
    public List<Recipe> findByCategoryName(String categoryName) {
        // 指定されたカテゴリに該当するレシピを取得するSQLを定義
        String query = "SELECT r FROM Recipe r JOIN r.category c WHERE c.name = :categoryName";

        return entityManager.createQuery(query, Recipe.class)
                .setParameter("categoryName", categoryName)
                .getResultList();
    }

    @Override
    public Recipe findById(int theId) {
        return entityManager.find(Recipe.class, theId);
    }

    @Override
    public Recipe save(Recipe theEmployee) {
        return null;
    }

    @Override
    public void deleteById(int theId) {

    }
}
