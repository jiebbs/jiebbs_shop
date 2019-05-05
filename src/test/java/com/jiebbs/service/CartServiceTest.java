package com.jiebbs.service;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-mvc.xml","classpath:spring-datasource.xml"})
public class CartServiceTest {

    @Resource(name = "iCartService")
    private ICartService iCartService;

    @Test
    public void addProductTest(){
        ServerResponse resp = iCartService.addProduct(22,26,100);
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void updateProduct(){
        ServerResponse resp = iCartService.updateProduct(22,26,100,0);
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void deleteProduct(){
        ServerResponse resp = iCartService.deleteProduct(22,26);
        JsonUtil.convert2JsonAndPrint(resp);
    }
}
