package com.jiebbs.service;

import com.alibaba.fastjson.JSONObject;
import com.jiebbs.common.Const;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.common.TokenCache;
import com.jiebbs.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-mvc.xml","classpath:spring-datasource.xml"})
public class UserServiceTest {

    @Resource(name="iUserService")
    private IUserService iUserService;

    @Test
    public void loginTest(){
        ServerResponse resp = iUserService.login("admin2","admin2");
        this.convertJsonAndPrint(resp);
    }

    @Test
    public void registerTest(){
        User user = new User();
        user.setUsername("admin4");
        user.setPassword("admin4");
        user.setEmail("jiebbs@126.com");
        user.setPhone("18676182810");
        user.setQuestion("你的生日");
        user.setAnswer("1992-04-27");
        ServerResponse resp = iUserService.register(user);
        this.convertJsonAndPrint(resp);
    }

    @Test
    public void vaildStrTest(){
        ServerResponse resp = iUserService.vaildStr("admin", Const.USERNAME);
        ServerResponse resp2 = iUserService.vaildStr("jiebbs@126.com",Const.EMAIL);
        this.convertJsonAndPrint(resp);
        this.convertJsonAndPrint(resp2);
    }

    @Test
    public void getForgetQuestionTest(){
        ServerResponse resp = iUserService.getForgetQuestion("admin");
        this.convertJsonAndPrint(resp);
    }

    @Test
    public void checkForgetAnswerTest(){
        ServerResponse resp = iUserService.checkForgetAnswer("admin2","我的生日","19920427");
        this.convertJsonAndPrint(resp);
    }

    @Test
    public void resetForgetPassword(){
        this.checkForgetAnswerTest();
        String token = TokenCache.getKeyValue(TokenCache.TOKEN_PREFIX+"admin2");
        ServerResponse resp = iUserService.resetForgetPassword("admin2",token,"123456");
        this.convertJsonAndPrint(resp);
    }





    private void convertJsonAndPrint(Object obj){
        String json = JSONObject.toJSONString(obj);
        System.out.println(json);
    }

}
