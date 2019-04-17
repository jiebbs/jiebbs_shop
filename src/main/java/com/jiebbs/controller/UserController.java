package com.jiebbs.controller;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 用户前台模块接口
 * @author weijie
 * @version 1.0 2019-04-17
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录接口
     * @param username 登录用户名
     * @param password 用户密码
     * @param session
     * @return 返回登录验证信息
     */
    @RequestMapping(value="login.do",method= RequestMethod.POST)
    @ResponseBody
    public Object login(String username, String password, HttpSession session){
        ServerResponse resp = iUserService.login(username,password);
        return resp;
    }
}
