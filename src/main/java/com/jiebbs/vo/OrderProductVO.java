package com.jiebbs.vo;

import com.jiebbs.pojo.OrderItem;

import java.util.List;

/**
 * 已勾选购物车商品值对象
 * @author weijie
 * @version v1.0 2019-05-14
 */
public class OrderProductVO {

    private List<OrderItem> orderItemList;

    private String imageHost;

    private String totalPrice;

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
