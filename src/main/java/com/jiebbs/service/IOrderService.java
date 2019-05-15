package com.jiebbs.service;

import com.github.pagehelper.PageInfo;
import com.jiebbs.common.ServerResponse;

import java.util.Map;

/**
 * 订单接口
 * @author weijie
 * @version v1.0 2019-05-08
 */
public interface IOrderService {

    ServerResponse createOrder(Integer userId,Integer shippingId);

    ServerResponse cancelOrder(Integer userId,Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse getOrderDetail(Integer userId,Long orderNo);

    ServerResponse getBackendOrderDetail(Long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer userId, Integer pageSize, Integer pageNum);

    ServerResponse pay(Integer userId, Long orderNo, String path);

    ServerResponse alipayCallback(Map<String,String> covertDateMap);

    ServerResponse<Boolean> queryOrderPayStatus(Integer userId,Long orderNo);

    ServerResponse send(Long orderNo);
}
