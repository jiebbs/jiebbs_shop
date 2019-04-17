package com.jiebbs.service.impl;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.dao.UserMapper;
import com.jiebbs.pojo.User;
import com.jiebbs.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户模块服务层实现类
 * @author weijie
 * @version 1.0 2019-04-17
 */
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        //校验用户名是否已经注册
        int result = userMapper.checkUsername(username);
        if(result>0) {
            //TODO 对传入密码进行MD5加密，以便于和数据库中存储的密码作比较

            User user = userMapper.checkLogin(username,password);
            if(null==user){
                return  ServerResponse.createByErrorMessage("密码错误");
            }
            //使用工具类常量，置空查询出来的用户密码信息
            user.setPassword(StringUtils.EMPTY);
            return ServerResponse.createBySuccessMessageAndData("用户登录成功",user);

        }else{
            return ServerResponse.createByErrorMessage("此用户名未注册");
        }
    }
}
