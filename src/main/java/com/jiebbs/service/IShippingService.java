package com.jiebbs.service;

import com.github.pagehelper.PageInfo;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.Shipping;
import com.jiebbs.vo.ShippingVO;

/**
 * 收货地址服务接口
 * @author weijie
 * @version v1.0 2019-05-06
 */
public interface IShippingService {

    ServerResponse addShippingAddress(Integer userId, Shipping shipping);

    ServerResponse deleteShippingAddress(Integer userId, Integer shippingId);

    ServerResponse updateShippingAddress(Integer userId, Shipping shipping);

    ServerResponse getShippingAddressDetail(Integer userId,Integer shippingId);

    ServerResponse<PageInfo<ShippingVO>> listShippingAddress(Integer userId, Integer pageNum, Integer pageSize);
}
