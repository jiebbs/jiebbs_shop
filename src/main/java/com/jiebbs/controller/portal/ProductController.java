package com.jiebbs.controller.portal;


import com.jiebbs.common.ServerResponse;
import com.jiebbs.service.IProductService;
import com.jiebbs.vo.ProductDetailVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 前台商品模块接口
 * @author weijie
 * @version v1.0 2019-04-26
 */
@Controller
@RequestMapping("/product/")
public class ProductController {

    @Resource(name = "iProductService")
    private IProductService iProductService;

    /**
     * 产品详情接口
     * （需要留言前端返回的数据需要校验查询的商品状态）
     * @param productId
     * @return
     */
    @RequestMapping(value = "get_product_detail.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVO> getProductDetail(Integer productId){

        return iProductService.getProductDetailProtal(productId);
    }




}
