package com.jiebbs.common;

/**
 * 常量类
 * @author weijie
 * @version 1.0 20190-04-19
 */
public class Const {

    public static final String CURRENT_USER="currentUser";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    /**
     * 使用内部接口，进行常量分组
     * 用户角色常量
     */
    public interface Role{

        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN =1; //管理员用户
    }
}
