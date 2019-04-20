package com.jiebbs.controller.backend;

import com.jiebbs.common.Const;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.User;
import com.jiebbs.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 后台用户接口模块
 * @author weijie
 * @version 1.0 2019-04-27
 */
@Controller
@RequestMapping("/manage/user/")
public class UserManagerController {

    @Resource(name = "iUserService")
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(HttpSession session, String username, String password){
        ServerResponse resp = iUserService.login(username,password);
        if(resp.isSuccess()){
            User user = (User)resp.getData();
            //验证登录的用户
            if(user.getRole()== Const.Role.ROLE_ADMIN){
                session.setAttribute(Const.CURRENT_USER,user);
            }else{
                return ServerResponse.createByErrorMessage("普通用户权限登录");
            }
        }
        return resp;
    }
}
