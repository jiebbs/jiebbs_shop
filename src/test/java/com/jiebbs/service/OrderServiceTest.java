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
public class OrderServiceTest {

    @Resource(name="iOrderService")
    private IOrderService iOrderService;

    @Test
    public void createOrderTest(){
        ServerResponse resp = iOrderService.createOrder(1,29);
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void payTest(){
        ServerResponse resp = iOrderService.pay(1,1491753014256L,"upload");
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void callbackTest(){
        ServerResponse resp = iOrderService.queryOrderPayStatus(1,1491753014256L);
        JsonUtil.convert2JsonAndPrint(resp);
    }
}
