package com.jiebbs.service;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.Shipping;
import com.jiebbs.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-mvc.xml","classpath:spring-datasource.xml"})
public class ShippingServiceTest {

    @Resource(name = "iShippingService")
    private IShippingService iShippingService;

    @Test
    public void addShippingAddress(){
        Shipping shipping = new Shipping();
        shipping.setReceiverName("收货人名字");
        shipping.setReceiverPhone("18000000000");
        shipping.setReceiverMobile("18000000000");
        shipping.setReceiverProvince("广东省");
        shipping.setReceiverCity("中山市");
        shipping.setReceiverDistrict("石岐区");
        shipping.setReceiverAddress("朗晴轩3号");
        shipping.setReceiverZip("528400");
        ServerResponse resp = iShippingService.addShippingAddress(22,shipping);
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void deleteShippingAddress(){
        ServerResponse resp = iShippingService.deleteShippingAddress(22,30);
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void updateShippingAddress(){
        Shipping shipping = new Shipping();
        shipping.setReceiverName("收货人名字");
        shipping.setReceiverPhone("18000000123");
        shipping.setReceiverMobile("18000000123");
        shipping.setReceiverProvince("广东省");
        shipping.setReceiverCity("中山市");
        shipping.setReceiverDistrict("石岐区");
        shipping.setReceiverAddress("朗晴轩4号");
        shipping.setReceiverZip("528433");
        ServerResponse resp = iShippingService.updateShippingAddress(22,31,shipping);
        JsonUtil.convert2JsonAndPrint(resp);
    }

}
