package com.myrecipes.backend.dao;

import com.myrecipes.backend.entity.Category;

public interface CategoryDAO {

    public Category findCategoryByName(String categoryName);
}
