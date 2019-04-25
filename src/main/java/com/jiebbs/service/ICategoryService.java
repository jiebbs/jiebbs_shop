package com.jiebbs.service;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.Category;

import java.util.List;
import java.util.Set;

/**
 * 产品分类接口
 * @author weijie
 * @version v1.0 2019-04-25
 */
public interface ICategoryService {

    ServerResponse<String> addCategory(String catetoryName,Integer parentId);

    ServerResponse<String> setCategoryName(Integer catetoryId,String newCategoryName);

    ServerResponse<List<Category>> getChildParallelCategory(Integer categoryId);

    ServerResponse<Set<Category>> getChildDeepCategory(Integer categoryId);
}
