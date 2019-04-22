package com.jiebbs.controller.portal;

import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
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
    @RequestMapping(value="valid_str.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> validStr(String str,String type){
        return iUserService.validStr(str,type);
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
    @RequestMapping(value="reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String oldPassword,String newPassword){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(null!=currentUser){
            return iUserService.resetPassword(currentUser,oldPassword,newPassword);
        }
        return ServerResponse.createByErrorMessage("当前用户不在登录状态");
    }

    /**
     * 更新用户信息接口
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value="update_userInfo.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(HttpSession session,User user){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(null!=currentUser){
            //用户id和用户名不能被更改
            user.setId(currentUser.getId());
            ServerResponse resp = iUserService.updateUserInfo(user);
            //更新Session中用户信息
            if(resp.isSuccess()){
                session.setAttribute(Const.CURRENT_USER,resp.getData());
            }
            return resp;
        }
        return ServerResponse.createByErrorMessage("当前用户不在登录状态");
    }

    /**
     * 获取用户个人详细信息
     * @param session
     * @return
     */
    @RequestMapping(value="get_userDetail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getInformation(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==currentUser){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                                                            ,"错误代码：10，用户未登录需要强制登录");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
