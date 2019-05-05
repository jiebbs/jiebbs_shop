package com.jiebbs.service;


import com.jiebbs.common.ServerResponse;
import com.jiebbs.vo.CartVO;

public interface ICartService {

    ServerResponse<CartVO> addProduct(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVO> updateProduct(Integer userId, Integer productId, Integer count,Integer checked);

    ServerResponse<CartVO> deleteProduct(Integer userId,Integer... productIds);
}
