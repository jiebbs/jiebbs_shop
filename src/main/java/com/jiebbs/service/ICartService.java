package com.jiebbs.service;


import com.jiebbs.common.ServerResponse;

public interface ICartService {

    ServerResponse<String> addProduct(Integer userId, Integer productId, Integer count);
}
