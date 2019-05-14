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

    /**
     * 商品状态Enum
     */
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

    /**
     * 订单状态Enum
     */
    public enum OrderStatusEnum{
        CANCALED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已支付"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSED(60,"订单关闭");

        OrderStatusEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        /**
         * 根据传入的code返回对应的文字描述
         * @param code
         * @return
         */
        public static String getOrderStatusEnumDesc(Integer code){
            for(OrderStatusEnum orderStatusEnum:values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum.getValue();
                }
            }
            return null;
        }
    }

    /**
     * 支付宝回调常量
     */
    public interface AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    /**
     * 支付平台enum
     */
    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");

        private int code;
        private String platfrom;

        PayPlatformEnum(int code, String platfrom) {
            this.code = code;
            this.platfrom = platfrom;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getPlatfrom() {
            return platfrom;
        }

        public void setPlatfrom(String platfrom) {
            this.platfrom = platfrom;
        }
    }

    public enum PaymentTypeEnum{
        PAY_ONLINE(1,"在线支付");
        private int code;
        private String desc;

        PaymentTypeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        /**
         * 根据传入的code返回对应的文字描述
         * @param code
         * @return
         */
        public static String getPaymentTypeEnumDesc(Integer code){
            for(PaymentTypeEnum paymentTypeEnum:values()){
                if(paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum.getDesc();
                }
            }
            return null;
        }
    }
}

