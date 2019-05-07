package com.jiebbs.dao;

import com.jiebbs.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByUserIdShippingId(@Param("userId") Integer userId, @Param("shippingId")Integer shippingId);

    int updateByPrimaryKeyUserIdSelective(Shipping record);

    Shipping selectShippingAddressDetail(@Param("userId") Integer userId,@Param("shippingId") Integer shippingId);

    List<Shipping> selectShippingAddressByUserId(@Param("userId")Integer userId);
}