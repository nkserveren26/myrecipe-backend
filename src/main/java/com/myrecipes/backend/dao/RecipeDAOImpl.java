package com.myrecipes.backend.dao;

import com.myrecipes.backend.dto.RecipeResponse;
import com.myrecipes.backend.entity.Recipe;
import com.myrecipes.backend.entity.RecipeIngredient;
import com.myrecipes.backend.entity.RecipeStep;
import com.myrecipes.backend.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

        return theQuery.getResultList();
    }

    // 特定のカテゴリに属するレシピの一覧を取得
    @Override
    public List<RecipeResponse> findByCategoryName(String categoryName) {
        // 指定されたカテゴリに該当するレシピを取得するSQLを定義
        String query = "SELECT new com.myrecipes.backend.dto.RecipeResponse(r.id, r.title, r.image, r.servings, r.videoUrl, r.createdAt) " +
                "FROM Recipe r JOIN r.category c WHERE c.name = :categoryName";

        return entityManager.createQuery(query, RecipeResponse.class)
                .setParameter("categoryName", categoryName)
                .getResultList();
    }

    // 特定のIDのレシピを検索
    @Override
    public Recipe findById(int theId) {
        return entityManager.find(Recipe.class, theId);
    }

    // レシピを新規追加
    @Override
    public void save(Recipe theRecipe) {
        entityManager.persist(theRecipe);
    }

    // 既存レシピを更新
    @Override
    public void update(Recipe theRecipe) {
        entityManager.merge(theRecipe);  // 既存のレシピを更新
    }

    @Override
    public void deleteById(int recipeId) {
        Recipe theRecipe = entityManager.find(Recipe.class, recipeId);

        if (theRecipe != null) {
            // 子エンティティの親参照を解除
            for (RecipeIngredient ingredient : theRecipe.getIngredients()) {
                ingredient.setRecipe(null);
            }
            for (RecipeStep step : theRecipe.getSteps()) {
                step.setRecipe(null);
            }
            if (theRecipe.getRecipePoint() != null) {
                theRecipe.getRecipePoint().setRecipe(null);
            }

            theRecipe.getIngredients().clear();
            theRecipe.getSteps().clear();
            theRecipe.setRecipePoint(null);

            if (theRecipe.getCategory() != null) {
                theRecipe.getCategory().getRecipes().remove(theRecipe);
                theRecipe.setCategory(null);
            }

            entityManager.remove(theRecipe); // 親エンティティを削除
            entityManager.flush();
        } else {
            throw new ResourceNotFoundException("Recipe not found with ID: " + recipeId);
        }
    }
}
