package com.jiebbs.service;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.User;


/**
 * 用户模块服务层接口
 * @author weijie
 * @version 1.0 2019-04-17
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> vaildStr(String str,String type);

    ServerResponse<String> getForgetQuestion(String username);

    ServerResponse<String> checkForgetAnswer(String username,String question,String answer);

    ServerResponse<String> resetForgetPassword(String username,String token,String newPassword);

    ServerResponse<String> resetPassword(User user,String oldPassword,String newPassword);

    ServerResponse<User> updateUserInfo(User user);

    ServerResponse<User> getInformation(Integer userId);
}
