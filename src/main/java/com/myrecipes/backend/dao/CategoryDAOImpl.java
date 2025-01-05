package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryDAOImpl implements CategoryDAO {

    private EntityManager entityManager;

    @Autowired
    public CategoryDAOImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }

    @Override
    public Category findCategoryByName(String categoryName) {

        // カテゴリ名で検索するクエリを作成
        String queryStr = "SELECT c FROM Category c WHERE c.name = :name";
        TypedQuery<Category> query = entityManager.createQuery(queryStr, Category.class);
        query.setParameter("name", categoryName);

        // 結果を取得。カテゴリが見つからない場合はnullを返す。
        Category category;
        try {
            category = query.getSingleResult();
        } catch (NoResultException e) {
            throw new RuntimeException("Category not found: " + categoryName, e);
        } catch (HibernateException e) {
            throw new RuntimeException("Hibernate exception occurred while fetching category: " + categoryName, e);
        }

        return category;
    }
}
