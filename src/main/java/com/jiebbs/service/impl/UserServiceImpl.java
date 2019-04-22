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
        int namevalidResult = userMapper.checkUsername(username);
        if(namevalidResult>0) {
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
        ServerResponse namevalidResp = this.validStr(user.getUsername(),Const.USERNAME);
        if(!namevalidResp.isSuccess()){
            return namevalidResp;
        }
        ServerResponse emailvalidResp = this.validStr(user.getEmail(),Const.EMAIL);
        if(!emailvalidResp.isSuccess()){
            return emailvalidResp;
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
    public ServerResponse<String> validStr(String str,String type){
        if(StringUtils.isNotBlank(type)&&StringUtils.isNotBlank(str)){
            int validResult = 0;
            //校验用户名是否存在
            if(StringUtils.equals(type,Const.USERNAME)){
                validResult = userMapper.checkUsername(str);
                return validResult>0? ServerResponse.<String>createByErrorMessage("用户名已注册"):
                        ServerResponse.<String>createBySuccessMessage("用户名未注册");

            }
            //校验邮箱是否存在
            if(StringUtils.equals(type,Const.EMAIL)){
                validResult = userMapper.checkEmail(str);
                return validResult>0? ServerResponse.<String>createByErrorMessage("邮箱已注册"):
                        ServerResponse.<String>createBySuccessMessage("邮箱未注册");
            }
        }
        return ServerResponse.createByErrorMessage("参数传入有误,请重新输入");
    }

    @Override
    public ServerResponse<String> getForgetQuestion(String username) {
        ServerResponse namevalid = this.validStr(username,Const.USERNAME);
        if(namevalid.isSuccess()){
            return namevalid;
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
        ServerResponse namevalid = this.validStr(username,Const.USERNAME);
        if(namevalid.isSuccess()){
            return namevalid;
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
            String MD5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int resetPwdResult = userMapper.updatePasswordByUsername(username,MD5Password);
            return resetPwdResult>0?ServerResponse.<String>createBySuccessMessage("密码重置成功")
                    :ServerResponse.<String>createByErrorMessage("出现异常，密码重置失败");
        }

        return ServerResponse.createByErrorMessage("Token校验失败,请重新获取");
    }

    @Override
    public ServerResponse<String> resetPassword(User user, String oldPassword, String newPassword) {
        //校验此当前用户旧密码
        String MD5oldPassword = MD5Util.MD5EncodeUtf8(oldPassword);
        int pwdCheckResult = userMapper.checkPassword(user.getId(),MD5oldPassword);
        if(pwdCheckResult>0){
            String MD5newPassword =MD5Util.MD5EncodeUtf8(newPassword);
            int pwdUpdateResult = userMapper.updatePasswordByUsername(user.getUsername(),MD5newPassword);
            return pwdUpdateResult>0?ServerResponse.<String>createBySuccessMessage("密码更新成功")
                    :ServerResponse.<String>createByErrorMessage("密码更新失败");
        }
        return ServerResponse.createByErrorMessage("旧密码错误");
    }

    @Override
    public ServerResponse<User> updateUserInfo(User user) {
        //校验邮箱是否已被占用（校验邮箱存在，并且对应的id不是本userId，则认为是占用了）
        int emailvalid = userMapper.checkEmailByUserId(user.getId(),user.getEmail());
        if(emailvalid>0){
            return ServerResponse.createByErrorMessage("邮箱已被占用");
        }
        //创建更新的中间对象，避免不必要字段被更新
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setPhone(user.getPhone());

        //通过校验后更新用户信息
        int infoUpdateResult = userMapper.updateByPrimaryKeySelective(updateUser);
        if(infoUpdateResult>0){
            //更新成功后，返回新的用户信息
            User newUserInfo = userMapper.selectByPrimaryKey(user.getId());
            newUserInfo.setPassword(StringUtils.EMPTY);
            return ServerResponse.createBySuccessMessageAndData("用户信息更新成功",newUserInfo);
        }
        return ServerResponse.createByErrorMessage("用户信息更新失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServerResponse.createByErrorMessage("查询不到当前用户");
        }
        user.setPassword("");
        return ServerResponse.createBySuccessMessageAndData("查询用户信息成功",user);
    }


    //后端服务


    @Override
    public ServerResponse<String> checkAdminRole(User user) {
        if(Const.Role.ROLE_ADMIN!=user.getRole()){
            return ServerResponse.createByErrorMessage("该用户不是管理员用户");
        }
        return ServerResponse.createBySuccess();
    }


}
