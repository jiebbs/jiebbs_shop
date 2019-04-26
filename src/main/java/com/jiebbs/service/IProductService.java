package com.jiebbs.service;

import com.github.pagehelper.PageInfo;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.Product;
import com.jiebbs.vo.ProductDetailVO;
import com.jiebbs.vo.ProductListVO;

/**
 * 商品服务接口
 * @author weijie
 * @version v1.0 2019-04-25
 */
public interface IProductService {

    ServerResponse<String> productSaveOrUpdate(Product product);

    ServerResponse<String> setProductStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVO> getProductDetailBackend(Integer productId);

    ServerResponse<PageInfo<ProductListVO>> getProducts(Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo<ProductListVO>> searchProducts(Integer productId,String productName,Integer pageNum, Integer pageSize);

    public ServerResponse<ProductDetailVO> getProductDetailProtal(Integer productId);
}
