package com.jiebbs.service;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.Product;

public interface IProductService {

    ServerResponse<String> productSaveOrUpdate(Product product);
}
