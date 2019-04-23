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
    public void productSaveOrUpdate(){

        Product product = new Product();
        product.setName("123");
        product.setCategoryId(0);
        product.setPrice(new BigDecimal(1));
        product.setStock(11);

        ServerResponse resp = iProductService.productSaveOrUpdate(product);
        JsonUtil.convert2JsonAndPrint(resp);
    }
}
