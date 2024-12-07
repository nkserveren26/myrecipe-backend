package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.RecipePoint;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RecipePointDAOImpl implements RecipePointDAO {

    private EntityManager entityManager;

    @Autowired
    public RecipePointDAOImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }

    @Override
    public RecipePoint findByRecipeId(int recipeId) {
        String jpql = "SELECT rp FROM RecipePoint rp WHERE rp.recipe.id = :recipeId";
        TypedQuery<RecipePoint> query = entityManager.createQuery(jpql, RecipePoint.class);
        query.setParameter("recipeId", recipeId);

        // 結果を取得し、存在しない場合は null を返す
        return query.getResultStream().findFirst().orElse(null);
    }

    @Override
    public void update(RecipePoint theRecipePoint) {
        entityManager.merge(theRecipePoint);
    }


}
