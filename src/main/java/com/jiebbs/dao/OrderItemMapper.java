package com.jiebbs.dao;

import com.jiebbs.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> selectByUserIdOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    List<OrderItem> selectByOrderNo(@Param("orderNo") Long orderNo);

    int brenchInsertOrderItem(@Param("orderItemList") List<OrderItem> orderItemList);

    int deleteByUserIdOrderNo(@Param("userId") Integer userId,@Param("orderNo")Long orderNo);
}