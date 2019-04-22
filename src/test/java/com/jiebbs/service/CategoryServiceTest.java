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
public class CategoryServiceTest {

    @Resource(name="iCategoryService")
    private ICategoryService iCategoryService;

    @Test
    public void addCategoryTest(){

        ServerResponse resp = iCategoryService.addCategory("中文商品",0);
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void setCategoryNameTest(){

        ServerResponse resp = iCategoryService.setCategoryName(100036,"西班牙商品");
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void getChildParallelCategoryTest(){

        ServerResponse resp = iCategoryService.getChildParallelCategory(100001);
        JsonUtil.convert2JsonAndPrint(resp);
    }
}
