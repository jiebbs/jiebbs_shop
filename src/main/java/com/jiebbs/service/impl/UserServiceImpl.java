package com.jiebbs.service.impl;

import com.jiebbs.common.Const;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.common.TokenCache;
import com.jiebbs.dao.UserMapper;
import com.jiebbs.pojo.User;
import com.jiebbs.service.IUserService;
import com.jiebbs.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 用户模块服务层实现类
 * @author weijie
 * @version 1.0 2019-04-17
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public ServerResponse<User> login(String username, String password) {
        //校验用户名是否已经注册
        int nameVaildResult = userMapper.checkUsername(username);
        if(nameVaildResult>0) {
            //对传入密码进行MD5加密，以便于和数据库中存储的密码作比较
            User user = userMapper.checkLogin(username,MD5Util.MD5EncodeUtf8(password));
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


    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse nameVaildResp = this.vaildStr(user.getUsername(),Const.USERNAME);
        if(!nameVaildResp.isSuccess()){
            return nameVaildResp;
        }
        ServerResponse emailVaildResp = this.vaildStr(user.getEmail(),Const.EMAIL);
        if(!emailVaildResp.isSuccess()){
            return emailVaildResp;
        }
        //设置用户角色
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密用户密码
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //插入用户
        int registerResult = userMapper.insertSelective(user);
        if(registerResult > 0){
            return ServerResponse.createBySuccessMessage("注册成功");
        }
        return ServerResponse.createByErrorMessage("注册异常，请重新尝试");
    }

    @Override
    public ServerResponse<String> vaildStr(String str,String type){
        if(StringUtils.isNotBlank(type)&&StringUtils.isNotBlank(str)){
            int vaildResult = 0;
            //校验用户名是否存在
            if(StringUtils.equals(type,Const.USERNAME)){
                vaildResult = userMapper.checkUsername(str);
                return vaildResult>0? ServerResponse.<String>createByErrorMessage("用户名已注册"):
                    ServerResponse.<String>createBySuccessMessage("用户名未注册");

            }
            //校验邮箱是否存在
            if(StringUtils.equals(type,Const.EMAIL)){
                vaildResult = userMapper.checkEmail(str);
                return vaildResult>0? ServerResponse.<String>createByErrorMessage("邮箱已注册"):
                        ServerResponse.<String>createBySuccessMessage("邮箱未注册");
            }
        }
        return ServerResponse.createByErrorMessage("参数传入有误,请重新输入");
    }

    @Override
    public ServerResponse<String> getForgetQuestion(String username) {
        ServerResponse nameVaild = this.vaildStr(username,Const.USERNAME);
        if(nameVaild.isSuccess()){
            return nameVaild;
        }
        String forgetQuestion = userMapper.selectForgetQuestion(username);
        if(StringUtils.isBlank(forgetQuestion)){
            return ServerResponse.createByErrorMessage("该用户没有设置忘记密码问题");
        }
        return ServerResponse.createBySuccessData(forgetQuestion);
    }

    @Override
    public ServerResponse<String> checkForgetAnswer(String username, String question, String answer) {
        int checkAnswerResult = userMapper.checkForgetAnswer(username,question,answer);
        if(checkAnswerResult>0){
            //忘记密码问题校验通过后，创建一个token返回给用户
            String token = UUID.randomUUID().toString();
            //TODO 后续改进考虑使用redis进行token的缓存
            //使用本地guava对token进行缓存(需要对token命名注意，使用token_用户名命名)
            TokenCache.setKeyValue(TokenCache.TOKEN_PREFIX+username,token);
            return ServerResponse.createBySuccessMessageAndData("忘记密码的问题回答正确",token);
        }
        return ServerResponse.createByErrorMessage("忘记密码的问题回答错误，请重新输入");
    }

    @Override
    public ServerResponse<String> resetForgetPassword(String username, String token, String newPassword) {
        //校验token
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("参数传递错误，token不能为空");
        }
        //校验用户名
        //TODO 此种校验方式有瑕疵，后续更新中寻找更加合适的校验方式
        ServerResponse nameVaild = this.vaildStr(username,Const.USERNAME);
        if(nameVaild.isSuccess()){
            return nameVaild;
        }
        //组装key
        String validKey = TokenCache.TOKEN_PREFIX+username;
        //尝试从本地缓存获取存储的token
        String validToken = TokenCache.getKeyValue(validKey);
        //校验本地缓存中的token是否过期
        if(StringUtils.isBlank(validToken)){
            return ServerResponse.createByErrorMessage("Token已失效");
        }
        //校验传入的token
        if(StringUtils.equals(validToken,token)){
            //对传入的密码进行MD5加密
            String newMD5Pwd = MD5Util.MD5EncodeUtf8(newPassword);
            int resetPwdResult = userMapper.updatePasswordByUsername(username,newMD5Pwd);
            return resetPwdResult>0?ServerResponse.<String>createBySuccessMessage("密码重置成功")
                    :ServerResponse.<String>createByErrorMessage("出现异常，密码重置失败");
        }

        return ServerResponse.createByErrorMessage("Token校验失败,请重新获取");
    }


}
