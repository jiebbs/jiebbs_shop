package com.jiebbs.controller;

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
 * 用户前台模块接口
 * @author weijie
 * @version 1.0 2019-04-17
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Resource(name = "iUserService")
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
    public ServerResponse login(String username, String password, HttpSession session){
        ServerResponse resp = iUserService.login(username,password);
        if(resp.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,resp.getData());
        }
        return resp;
    }

    /**
     * 用户登出接口
     * @param session
     * @return
     */
    @RequestMapping(value="logout.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册接口
     * @param user
     * @return
     */
    @RequestMapping(value="register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return  iUserService.register(user);
    }

    /**
     * 校验传入参数接口
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value="vaild_str.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> vaildStr(String str,String type){
        return iUserService.vaildStr(str,type);
    }

    /**
     * 获取用户个人信息接口
     * @param session
     * @return
     */
    @RequestMapping(value="get_user_info.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        return null!=user?
                ServerResponse.createBySuccessData(user):
                ServerResponse.<User>createByErrorMessage("用户未登录，无法获取用户信息");
    }

    /**
     * 获取忘记密码问题接口
     * @param username 用户名
     * @return
     */
    @RequestMapping(value="get_forget_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getForgetQuestion(String username){
        return iUserService.getForgetQuestion(username);
    }

    /**
     * 校验忘记密码答案接口
     * @param username 用户名
     * @param question 忘记密码问题
     * @param answer 忘记密码答案
     * @return
     */
    @RequestMapping(value="check_forget_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkForgetAnswer(String username,String question,String answer){
        return iUserService.checkForgetAnswer(username,question,answer);
    }

    /**
     * 重置密码接口
     * @param username 用户名
     * @param token 重置密码所需的令牌
     * @param newPassword 新的密码
     * @return
     */
    @RequestMapping(value="reset_forget_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetForgetPassword(String username,String token,String newPassword){
        return iUserService.resetForgetPassword(username,token,newPassword);
    }

    /**
     * 登录状态下重置密码接口
     * @param session
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return
     */
    public ServerResponse resetPassword(HttpSession session,String oldPassword,String newPassword){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        //TODO 还没有完成
        return ServerResponse.createByErrorMessage("当前用户不在登录状态");
    }
}
