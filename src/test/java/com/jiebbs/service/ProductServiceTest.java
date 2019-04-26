package com.jiebbs.service;


import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.Product;
import com.jiebbs.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-mvc.xml","classpath:spring-datasource.xml"})
public class ProductServiceTest {

    @Resource(name="iProductService")
    private IProductService iProductService;

    @Test
    public void productSaveOrUpdateTest(){

        Product product = new Product();
        product.setId(30);
        product.setName("321");
        product.setCategoryId(0);
        product.setPrice(new BigDecimal(1));
        product.setStock(11);

        ServerResponse resp = iProductService.productSaveOrUpdate(product);
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void setProductStatusTest(){

        ServerResponse resp = iProductService.setProductStatus(30,2);
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void getProductDetailBackendTest(){
        ServerResponse resp = iProductService.getProductDetailBackend(30);
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void getProductsTest(){
        ServerResponse resp = iProductService.getProducts(1,10);
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void searchProductsByIdNameTest(){
        ServerResponse resp = iProductService.searchProducts(null,"冰箱",1,10);
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void getProductDetailPortal(){
        ServerResponse resp = iProductService.getProductDetailProtal(26);
        JsonUtil.convert2JsonAndPrint(resp);
    }
}
