package com.jiebbs.service;

import com.jiebbs.common.Const;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.common.TokenCache;
import com.jiebbs.pojo.User;
import com.jiebbs.util.JsonUtil;
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
        JsonUtil.convert2JsonAndPrint(resp);
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
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void validStrTest(){
        ServerResponse resp = iUserService.validStr("admin", Const.USERNAME);
        ServerResponse resp2 = iUserService.validStr("jiebbs@126.com",Const.EMAIL);
        JsonUtil.convert2JsonAndPrint(resp);
        JsonUtil.convert2JsonAndPrint(resp2);
    }

    @Test
    public void getForgetQuestionTest(){
        ServerResponse resp = iUserService.getForgetQuestion("admin");
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void checkForgetAnswerTest(){
        ServerResponse resp = iUserService.checkForgetAnswer("admin2","我的生日","19920427");
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void resetForgetPassword(){
        this.checkForgetAnswerTest();
        String token = TokenCache.getKeyValue(TokenCache.TOKEN_PREFIX+"admin2");
        ServerResponse resp = iUserService.resetForgetPassword("admin2",token,"123456");
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void resetPassword(){
        User user = new User();
        user.setId(22);
        user.setUsername("admin2");
        ServerResponse resp = iUserService.resetPassword(user,"123456","admin2");
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void updateUserInfoTest(){
        User user = new User();
        user.setId(22);
        user.setEmail("jiebbs3@126.com");
        user.setPhone("138001380000");
        ServerResponse resp = iUserService.updateUserInfo(user);
        JsonUtil.convert2JsonAndPrint(resp);
    }

    @Test
    public void getInformationTest(){
        ServerResponse resp = iUserService.getInformation(22);
        JsonUtil.convert2JsonAndPrint(resp);
    }
}
