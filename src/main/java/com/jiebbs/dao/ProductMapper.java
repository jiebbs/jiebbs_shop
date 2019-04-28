package com.jiebbs.dao;

import com.jiebbs.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    int updateProductStatusById(@Param("productId") Integer productId, @Param("status") Integer status);

    List<Product> getProductsByCategoryId();

    List<Product> searchProductsByIdName(@Param("productId") Integer productId,@Param("productName") String productName);

    int checkProductStatus(Integer productId);

    List<Product> searchProductsByCategoryIdsName(@Param("categoryIdList")List<Integer> categoryId,@Param("productName")String productName);
}