package com.jiebbs.common;

import com.google.common.collect.Sets;

import java.util.Set;

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

    /**
     * 排序条件常量
     */
    public interface ProductOrderByCondition{
        Set<String> PRICE_ASE_DESC = Sets.newHashSet("price_asc","price_desc");
    }

    /**
     * 购物车商品勾选状态常量
     */
    public interface CartProductStatus{
        Integer CHECKED = 1;
        Integer NOT_CHECKED = 0;

        String LIMIT_QUANTITY_FAIL = "LIMIT_QUANTITY_FAIL";
        String LIMIT_QUANTITY_SUCCESS = "LIMIT_QUANTITY_SUCCESS";
    }

    public enum ProductStatusEnum{
        ON_SALE(1, "已上架"),
        NOT_ON_SALE(2, "已下架"),
        DELETED(3, "已删除");

        private int status;
        private String desc;

        ProductStatusEnum( int status, String desc){
            this.status = status;
            this.desc = desc;
        }

        public int getStatus () {
            return status;
        }

        public String getDesc () {
            return desc;
         }
    }

}

