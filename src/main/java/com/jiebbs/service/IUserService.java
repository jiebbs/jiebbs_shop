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
}
