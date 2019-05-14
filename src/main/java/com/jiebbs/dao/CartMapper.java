package com.jiebbs.dao;

import com.alipay.api.domain.Car;
import com.jiebbs.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);

    List<Cart> selectCartByUserId(Integer userId);

    int selectCartCheckedAllStatusByUserId(Integer userId);

    int deleteCartByUserIdProductIds(@Param("userId") Integer userId,@Param("productIds") List<String> productIds);

    int selectCartProductCountByUserId(Integer userId);

    List<Cart> selectCheckedCartByUserId(Integer userId);

    int brenchDeleteCartByCartId(@Param("cartList") List<Cart> cartList);

}
