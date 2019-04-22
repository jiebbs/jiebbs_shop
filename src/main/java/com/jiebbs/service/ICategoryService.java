package com.jiebbs.service;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.Category;

import java.util.List;

public interface ICategoryService {

    ServerResponse<String> addCategory(String catetoryName,Integer parentId);

    ServerResponse<String> setCategoryName(Integer catetoryId,String newCategoryName);

    ServerResponse<List<Category>> getChildParallelCategory(Integer categoryId);
}
