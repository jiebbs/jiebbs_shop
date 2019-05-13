package com.jiebbs.service;

import com.jiebbs.common.ServerResponse;

import java.util.Map;

/**
 * 订单接口
 * @author weijie
 * @version v1.0 2019-05-08
 */
public interface IOrderService {

    ServerResponse pay(Integer userId, Long orderNo, String path);

    ServerResponse alipayCallback(Map<String,String> covertDateMap);

    ServerResponse<Boolean> queryOrderPayStatus(Integer userId,Long orderNo);
}
